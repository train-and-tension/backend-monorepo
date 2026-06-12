package com.traintension.core.model.sync;

import com.traintension.core.model.sync.SyncDTO.Deleted;
import com.traintension.core.model.sync.SyncDTO.Event;
import com.traintension.core.model.sync.SyncDTO.Operation;
import com.traintension.core.model.sync.SyncDTO.Response;
import com.traintension.core.model.sync.SyncDTO.Scope;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.traintension.core.generated.Tables.*;

@Service
@RequiredArgsConstructor
public class SyncService {
    private static final int DEFAULT_LIMIT = 150;
    private static final int MAX_LIMIT = 150;

    private final DSLContext dsl;

    @Transactional(readOnly = true)
    public Response sync(UUID userId, Long afterVersion, Integer requestedLimit) {
        long cursor = afterVersion == null ? 0L : afterVersion;
        int limit = normalizeLimit(requestedLimit);
        int fetchLimit = limit + 1;

        List<Event> events = new ArrayList<>();

        events.addAll(fetchUpserts(USER_PROFILE, USER_PROFILE.ID, USER_PROFILE.VERSION, USER_PROFILE.ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(BODY_INFORMATION, BODY_INFORMATION.ID, BODY_INFORMATION.VERSION, BODY_INFORMATION.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(MEASUREMENT_HISTORY, MEASUREMENT_HISTORY.ID, MEASUREMENT_HISTORY.VERSION, MEASUREMENT_HISTORY.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(MUSCLE, MUSCLE.ID, MUSCLE.VERSION, DSL.trueCondition(), cursor, fetchLimit));
        events.addAll(fetchUpserts(EQUIPMENT, EQUIPMENT.ID, EQUIPMENT.VERSION, ownedOrSystem(EQUIPMENT.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(EXERCISE, EXERCISE.ID, EXERCISE.VERSION, ownedOrSystem(EXERCISE.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(EXERCISE_MUSCLE, EXERCISE_MUSCLE.ID, EXERCISE_MUSCLE.VERSION, ownedOrSystem(EXERCISE_MUSCLE.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(SAVED_EXERCISE, SAVED_EXERCISE.ID, SAVED_EXERCISE.VERSION, SAVED_EXERCISE.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(WORKOUT_PROGRAM, WORKOUT_PROGRAM.ID, WORKOUT_PROGRAM.VERSION, ownedOrSystem(WORKOUT_PROGRAM.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(WORKOUT_DAY, WORKOUT_DAY.ID, WORKOUT_DAY.VERSION, ownedOrSystem(WORKOUT_DAY.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(TARGET_SET, TARGET_SET.ID, TARGET_SET.VERSION, ownedOrSystem(TARGET_SET.USER_PROFILE_ID, userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(WORKOUT_PERIOD, WORKOUT_PERIOD.ID, WORKOUT_PERIOD.VERSION, WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(WORKOUT_SESSION, WORKOUT_SESSION.ID, WORKOUT_SESSION.VERSION, WORKOUT_SESSION.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchUpserts(SET_RESULT, SET_RESULT.ID, SET_RESULT.VERSION, SET_RESULT.USER_PROFILE_ID.eq(userId), cursor, fetchLimit));
        events.addAll(fetchDeletes(userId, cursor, fetchLimit));

        events.sort(Comparator.comparing(Event::version));

        boolean hasNext = events.size() > limit;
        Long nextPageFirstVersion = hasNext ? events.get(limit).version() : null;
        List<Event> pageItems = hasNext ? events.subList(0, limit) : events;
        Long nextAfterVersion = pageItems.isEmpty() ? cursor : pageItems.getLast().version();
        List<Event> immutableItems = List.copyOf(pageItems);

        return new Response(
                immutableItems,
                new Scope(immutableItems.stream().filter(this::isSystemEvent).toList()),
                new Scope(immutableItems.stream().filter(event -> !isSystemEvent(event)).toList()),
                hasNext,
                nextAfterVersion,
                nextPageFirstVersion
        );
    }

    private int normalizeLimit(Integer requestedLimit) {
        if (requestedLimit == null) {
            return DEFAULT_LIMIT;
        }

        return Math.clamp(requestedLimit, 1, MAX_LIMIT);
    }

    private Condition ownedOrSystem(TableField<?, UUID> userProfileIdField, UUID userId) {
        return userProfileIdField.isNull().or(userProfileIdField.eq(userId));
    }

    private boolean isSystemEvent(Event event) {
        if (event.operation() == Operation.DELETE) {
            return event.deleted() != null && event.deleted().userProfileId() == null;
        }

        if (event.data() != null && event.data().containsKey("user_profile_id")) {
            return event.data().get("user_profile_id") == null;
        }

        return MUSCLE.getName().equals(event.tableName());
    }

    private <R extends org.jooq.Record> List<Event> fetchUpserts(
            Table<R> table,
            TableField<R, UUID> idField,
            TableField<R, Long> versionField,
            Condition ownerCondition,
            long afterVersion,
            int fetchLimit
    ) {
        return dsl.selectFrom(table)
                .where(versionField.gt(afterVersion))
                .and(ownerCondition)
                .orderBy(versionField.asc())
                .limit(fetchLimit)
                .fetch(record -> new Event(
                        record.get(versionField),
                        table.getName(),
                        Operation.UPSERT,
                        record.get(idField),
                        toData(record),
                        null
                ));
    }

    private List<Event> fetchDeletes(UUID userId, long afterVersion, int fetchLimit) {
        return dsl.selectFrom(DELETED_HISTORY)
                .where(DELETED_HISTORY.VERSION.gt(afterVersion))
                .and(DELETED_HISTORY.USER_PROFILE_ID.eq(userId).or(DELETED_HISTORY.USER_PROFILE_ID.isNull()))
                .orderBy(DELETED_HISTORY.VERSION.asc())
                .limit(fetchLimit)
                .fetch(record -> {
                    Deleted deleted = new Deleted(
                            record.getTableName(),
                            record.getRecordId(),
                            record.getUserProfileId(),
                            record.getVersion(),
                            record.getCreatedAt()
                    );

                    return new Event(
                            record.getVersion(),
                            DELETED_HISTORY.getName(),
                            Operation.DELETE,
                            record.getRecordId(),
                            null,
                            deleted
                    );
                });
    }

    private Map<String, Object> toData(org.jooq.Record record) {
        Map<String, Object> data = new LinkedHashMap<>();
        for (Field<?> field : record.fields()) {
            data.put(field.getName(), record.get(field));
        }
        return data;
    }
}
