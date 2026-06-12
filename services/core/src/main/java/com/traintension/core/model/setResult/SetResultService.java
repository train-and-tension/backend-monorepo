package com.traintension.core.model.setResult;

import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.tables.records.SetResultRecord;
import com.traintension.core.model.setResult.SetResultDTO.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.traintension.core.generated.Tables.*;

@Service
@RequiredArgsConstructor
public class SetResultService {
    private final DSLContext dsl;

    @Transactional
    public List<Response> addResults(AddResultsRequest request, UUID userId) {
        validateWorkoutSession(request.workoutSessionId(), userId);

        List<SetResultRecord> records = request.items().stream()
                .map(item -> fromCreateRequest(item, request.workoutSessionId(), userId))
                .toList();

        return dsl.insertInto(SET_RESULT)
                .set(records)
                .returning()
                .fetch(SetResultMapper::fromRecord);
    }

    @Transactional
    public List<Response> updateResults(UpdateResultsRequest request, UUID userId) {
        List<Response> responses = request.items().stream()
                .map(item -> {
                    SetResultRecord record = new SetResultRecord();
                    if (item.exerciseId() != null) {
                        var exercise = findExercise(item.exerciseId(), userId);
                        record.setExerciseId(item.exerciseId());
                        record.setExerciseNameSnapshot(exercise.name());
                    }
                    if (item.orderNumber() != null) record.setOrderNumber(item.orderNumber());
                    if (item.duration() != null) record.setDuration(item.duration());
                    if (item.restDuration() != null) record.setRestDuration(item.restDuration());
                    if (item.repCount() != null) record.setRepCount(item.repCount());
                    if (item.weight() != null) record.setWeightKg(item.weight());
                    if (item.unit() != null) record.setUnit(item.unit());

                    SetResultRecord updated = dsl.update(SET_RESULT)
                            .set(record)
                            .where(SET_RESULT.ID.eq(item.id()))
                            .and(SET_RESULT.USER_PROFILE_ID.eq(userId))
                            .returning()
                            .fetchOne();

                    if (updated == null) {
                        throw new NotFoundException();
                    }
                    return SetResultMapper.fromRecord(updated);
                })
                .toList();

        if (responses.size() != request.items().size()) {
            throw new NotFoundException();
        }
        return responses;
    }

    @Transactional
    public List<UUID> deleteResults(Set<UUID> ids, UUID userId) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> deletedIds = dsl.deleteFrom(SET_RESULT)
                .where(SET_RESULT.ID.in(ids))
                .and(SET_RESULT.USER_PROFILE_ID.eq(userId))
                .returning(SET_RESULT.ID)
                .fetch(SET_RESULT.ID);

        if (deletedIds.size() != ids.size()) {
            throw new NotFoundException();
        }
        return deletedIds;
    }

    public SetResultRecord fromCreateRequest(CreateRequest request, UUID workoutSessionId, UUID userId) {
        var exercise = findExercise(request.exerciseId(), userId);

        SetResultRecord record = new SetResultRecord();
        record.setUserProfileId(userId);
        record.setWorkoutSessionId(workoutSessionId);
        record.setExerciseId(request.exerciseId());
        record.setOrderNumber(request.orderNumber());
        record.setDuration(request.duration());
        record.setRestDuration(request.restDuration());
        record.setRepCount(request.repCount());
        record.setWeightKg(request.weight());
        record.setUnit(request.unit());
        record.setExerciseNameSnapshot(exercise.name());
        record.setTargetedRepCount(request.repCount() == null ? 0 : request.repCount());
        record.setTargetedWeight(request.weight());
        record.setTargetedDuration(request.duration());
        return record;
    }

    private void validateWorkoutSession(UUID workoutSessionId, UUID userId) {
        boolean exists = dsl.fetchExists(
                dsl.selectOne()
                        .from(WORKOUT_SESSION)
                        .where(WORKOUT_SESSION.ID.eq(workoutSessionId))
                        .and(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
        );
        if (!exists) {
            throw new NotFoundException();
        }
    }

    private ExerciseSnapshot findExercise(UUID exerciseId, UUID userId) {
        return dsl.select(EXERCISE.NAME)
                .from(EXERCISE)
                .where(EXERCISE.ID.eq(exerciseId))
                .and(EXERCISE.USER_PROFILE_ID.isNull().or(EXERCISE.USER_PROFILE_ID.eq(userId)))
                .fetchOptional()
                .map(r -> new ExerciseSnapshot(r.value1()))
                .orElseThrow(NotFoundException::new);
    }

    private record ExerciseSnapshot(String name) {}
}
