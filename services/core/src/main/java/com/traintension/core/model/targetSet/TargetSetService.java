package com.traintension.core.model.targetSet;

import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.tables.records.TargetSetRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import com.traintension.core.model.targetSet.TargetSetDTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.traintension.core.generated.Tables.TARGET_SET;
import static com.traintension.core.generated.Tables.WORKOUT_DAY;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "target-sets-cache")
public class TargetSetService {
    private final DSLContext dsl;


    //--------------------------------------------------------------------------------//
    //                              🛠️ADMIN METHODS🛠️
    //--------------------------------------------------------------------------------//

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay createGlobalTargetSets(
            CreateGlobalsRequest request,
            UUID workoutProgramId,
            UUID workoutDayId
    ) {
        validateWorkoutDay(workoutProgramId, workoutDayId, null);

        List<TargetSetRecord> targetSetRecords = dsl.insertInto(TARGET_SET)
                .set(TargetSetMapper.fromDTO(request, workoutDayId, getMaxTargetSetOrder(workoutDayId, null)))
                .returning()
                .fetch();

        return TargetSetMapper.fromRecord(workoutDayId, targetSetRecords);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay updateGlobalTargetSets(
            UpdateGlobalsRequest request,
            UUID workoutProgramId,
            UUID workoutDayId
    ) {
        List<TargetSetRecord> targetSetRecords = TargetSetMapper.fromDTO(request);
        UUID[] targetSetIds = extractTargetSetRecordIds(targetSetRecords);

        validateWorkoutDay(workoutProgramId, workoutDayId, null);
        updateTargetSets(workoutDayId, targetSetRecords, null);
        List<TargetSetRecord> updatedRecords = findTargetSetsByWorkoutDayId(
                workoutDayId,
                null,
                targetSetIds
        );
        return TargetSetMapper.fromRecord(workoutDayId, updatedRecords);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay reorderGlobalTargetSets(
            ReorderGlobalsRequest reorderGlobalsRequest,
            UUID workoutProgramId,
            UUID workoutDayId
    ) {
        validateWorkoutDay(workoutProgramId, workoutDayId, null);

        List<TargetSetRecord> recordsToUpdate = TargetSetMapper.fromDTO(reorderGlobalsRequest);
        UUID[] idsToCheck = extractTargetSetRecordIds(recordsToUpdate);

        validateTargetSetsExistence(workoutDayId, null, idsToCheck);

        updateTargetSets(workoutDayId, recordsToUpdate, null);
        List<TargetSetRecord> allTargetSetByWorkoutDay = findTargetSetsByWorkoutDayId(workoutDayId, null);

        return TargetSetMapper.fromRecord(workoutDayId, allTargetSetByWorkoutDay);

    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteGlobalTargetSets(UUID workoutProgramId, UUID workoutDayId, Set<UUID> ids) {
        validateWorkoutDay(workoutProgramId, workoutDayId, null);
        if (ids == null || ids.isEmpty()) {
            return;
        }

        validateTargetSetsExistence(workoutDayId, null, ids.toArray(new UUID[0]));

        int deletedCount = dsl.deleteFrom(TARGET_SET)
                .where(TARGET_SET.ID.in(ids))
                .and(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                .and(userCondition(null))
                .execute();

        if (deletedCount != ids.size()) {
            throw new NotFoundException("Some target sets could not be deleted.");
        }
    }

    @Transactional(readOnly = true)
    public ResponsesByWorkoutDay getGlobalTargetSetsByWorkoutDay(UUID workoutProgramId, UUID workoutDayId) {
        validateWorkoutDay(workoutProgramId, workoutDayId, null);
        List<TargetSetRecord> records = findTargetSetsByWorkoutDayId(workoutDayId, null);
        return TargetSetMapper.fromRecord(workoutDayId, records);
    }

    @Transactional(readOnly = true)
    public List<Response> getGlobalTargetSetsByIds(Set<UUID> ids) {
        List<TargetSetRecord> records = dsl.selectFrom(TARGET_SET)
                .where(TARGET_SET.ID.in(ids))
                .and(TARGET_SET.USER_PROFILE_ID.isNull())
                .fetch();
        return records.stream().map(TargetSetMapper::fromRecord).toList();
    }

    @Transactional(readOnly = true)
    public ResponsesByWorkoutProgram getGlobalTargetSetsByWorkoutProgram(UUID workoutProgramId) {
        List<TargetSetRecord> records = dsl.selectFrom(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.in(
                        DSL.select(WORKOUT_DAY.ID)
                                .from(WORKOUT_DAY)
                                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                ))
                .and(userCondition(null))
                .fetch();

        Map<UUID, List<TargetSetRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(TargetSetRecord::getWorkoutDayId));

        return TargetSetMapper.fromRecord(workoutProgramId, grouped);
    }

    //--------------------------------------------------------------------------------//
    //                              🧑USER METHODS🧑
    //--------------------------------------------------------------------------------//

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay createPersonalTargetSets(
            CreatePersonalsRequest request,
            UUID workoutProgramId,
            UUID workoutDayId,
            UUID userId
    ) {
        validateWorkoutDay(workoutProgramId, workoutDayId, userId);

        List<TargetSetRecord> targetSetRecords = dsl.insertInto(TARGET_SET)
                .set(TargetSetMapper.fromDTO(request, workoutDayId, userId, getMaxTargetSetOrder(workoutDayId, userId)))
                .returning()
                .fetch();

        return TargetSetMapper.fromRecord(workoutDayId, targetSetRecords);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay updatePersonalTargetSets(
            UpdatePersonalsRequest request,
            UUID workoutProgramId,
            UUID workoutDayId,
            UUID userId
    ) {
        List<TargetSetRecord> targetSetRecords = TargetSetMapper.fromDTO(request);
        UUID[] targetSetIds = extractTargetSetRecordIds(targetSetRecords);

        validateWorkoutDay(workoutProgramId, workoutDayId, userId);
        updateTargetSets(workoutDayId, targetSetRecords, userId);
        List<TargetSetRecord> updatedRecords = findTargetSetsByWorkoutDayId(
                workoutDayId,
                userId,
                targetSetIds
        );
        return TargetSetMapper.fromRecord(workoutDayId, updatedRecords);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponsesByWorkoutDay reorderPersonalTargetSets(
            ReorderPersonalsRequest request,
            UUID workoutProgramId,
            UUID workoutDayId,
            UUID userId
    ) {
        validateWorkoutDay(workoutProgramId, workoutDayId, userId);

        List<TargetSetRecord> recordsToUpdate = TargetSetMapper.fromDTO(request);
        UUID[] idsToCheck = extractTargetSetRecordIds(recordsToUpdate);

        validateTargetSetsExistence(workoutDayId, userId, idsToCheck);

        updateTargetSets(workoutDayId, recordsToUpdate, userId);
        List<TargetSetRecord> allTargetSetByWorkoutDay = findTargetSetsByWorkoutDayId(workoutDayId, userId);

        return TargetSetMapper.fromRecord(workoutDayId, allTargetSetByWorkoutDay);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deletePersonalTargetSets(UUID workoutProgramId, UUID workoutDayId, Set<UUID> ids, UUID userId) {
        validateWorkoutDay(workoutProgramId, workoutDayId, userId);

        if (ids == null || ids.isEmpty()) {
            return;
        }

        validateTargetSetsExistence(workoutDayId, userId, ids.toArray(new UUID[0]));
        validateTargetSetsRemainAfterDelete(workoutDayId, userId, ids);

        int deletedCount = dsl.deleteFrom(TARGET_SET)
                .where(TARGET_SET.ID.in(ids))
                .and(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                .and(userCondition(userId))
                .execute();

        if (deletedCount != ids.size()) {
            throw new NotFoundException("Some target sets could not be deleted.");
        }
    }

    @Transactional(readOnly = true)
    public ResponsesByWorkoutDay getPersonalTargetSetsByWorkoutDay(UUID workoutProgramId, UUID workoutDayId, UUID userId) {
        validateWorkoutDay(workoutProgramId, workoutDayId, userId);
        List<TargetSetRecord> records = findTargetSetsByWorkoutDayId(workoutDayId, userId);
        return TargetSetMapper.fromRecord(workoutDayId, records);
    }

    @Transactional(readOnly = true)
    public List<Response> getPersonalTargetSetsByIds(Set<UUID> ids, UUID userId) {
        List<TargetSetRecord> records = dsl.selectFrom(TARGET_SET)
                .where(TARGET_SET.ID.in(ids))
                .and(TARGET_SET.USER_PROFILE_ID.eq(userId))
                .fetch();
        return records.stream().map(TargetSetMapper::fromRecord).toList();
    }

    @Transactional(readOnly = true)
    public ResponsesByWorkoutProgram getPersonalTargetSetsByWorkoutProgram(UUID workoutProgramId, UUID userId) {
        List<TargetSetRecord> records = dsl.selectFrom(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.in(
                        DSL.select(WORKOUT_DAY.ID)
                                .from(WORKOUT_DAY)
                                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                ))
                .and(userCondition(userId))
                .fetch();

        Map<UUID, List<TargetSetRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(TargetSetRecord::getWorkoutDayId));

        return TargetSetMapper.fromRecord(workoutProgramId, grouped);
    }
    //--------------------------------------------------------------------------------//
    //                              🔧PRIVATE METHODS🔧
    //--------------------------------------------------------------------------------//

    private Condition userCondition(UUID userId) {
        return userId == null
                ? TARGET_SET.USER_PROFILE_ID.isNull()
                : TARGET_SET.USER_PROFILE_ID.eq(userId);
    }

    @Transactional(readOnly = true)
    private void validateWorkoutDay(UUID workoutProgramId, UUID workoutDayId, UUID userId) {
        boolean workoutDayExists = dsl.fetchExists(
                dsl.selectFrom(WORKOUT_DAY)
                        .where(WORKOUT_DAY.ID.eq(workoutDayId))
                        .and(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                        // 🔥 Helper metot kullanımı (WORKOUT_DAY için)
                        .and(userId == null ? WORKOUT_DAY.USER_PROFILE_ID.isNull() : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
        );

        if (!workoutDayExists) throw new NotFoundException("Workout Day not found for related Workout Program!");
    }

    @Transactional(readOnly = true)
    private void validateTargetSetsExistence(UUID workoutDayId, UUID userId, UUID... targetSetIds) {
        if (targetSetIds == null || targetSetIds.length == 0) {
            throw new IllegalArgumentException("Target set IDs cannot be null or empty.");
        }

        List<UUID> dbSetIds = dsl.select(TARGET_SET.ID)
                .from(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                .and(userCondition(userId)) // 🔥 Temizlendi
                .fetchInto(UUID.class);

        // 🔥 Unutulan boyut kontrolü eklendi
        if (dbSetIds.size() != targetSetIds.length) {
            throw new NotFoundException("Target set count mismatch for the specified workout day.");
        }

        Set<UUID> inputIds = Arrays.stream(targetSetIds).collect(Collectors.toSet());

        if (!inputIds.containsAll(dbSetIds)) {
            throw new NotFoundException("Some target set IDs are invalid or do not belong to this workout day.");
        }
    }

    @Transactional
    private void updateTargetSets(UUID workoutDayId, List<TargetSetRecord> targetSetRecords, UUID userId) {
        var queries = targetSetRecords.stream()
                .map(record -> dsl.update(TARGET_SET)
                        .set(record)
                        .where(TARGET_SET.ID.eq(record.getId()))
                        .and(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                        .and(userCondition(userId)) // 🔥 Temizlendi
                ).toList();

        int[] results = dsl.batch(queries).execute();

        int totalUpdated = Arrays.stream(results).sum();
        if (totalUpdated != targetSetRecords.size()) {
            throw new NotFoundException("Some target sets not found or do not belong to the specified workout day and user");
        }
    }

    private List<TargetSetRecord> findTargetSetsByWorkoutDayId(UUID workoutDayId, UUID userId, UUID... targetSetIds) {
        return dsl.selectFrom(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                .and(userCondition(userId)) // 🔥 Temizlendi
                .and(targetSetIds.length == 0
                        ? DSL.noCondition()
                        : TARGET_SET.ID.in(targetSetIds))
                .fetch();
    }

    @Transactional(readOnly = true)
    private UUID[] extractTargetSetRecordIds(List<TargetSetRecord> records) {
        return records.stream()
                .map(TargetSetRecord::getId)
                .toArray(UUID[]::new);
    }

    @Transactional(readOnly = true)
    private Integer getMaxTargetSetOrder(UUID workoutDayId, UUID userId) {
        Integer maxOrder = dsl.select(DSL.max(TARGET_SET.ORDER_NUMBER))
                .from(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                .and(userCondition(userId)) // 🔥 Temizlendi
                .fetchOneInto(Integer.class);

        return maxOrder == null ? 0 : maxOrder;
    }

    private void validateTargetSetsRemainAfterDelete(UUID workoutDayId, UUID userId, Set<UUID> idsToDelete) {
        int remainingCount = dsl.fetchCount(
                dsl.selectFrom(TARGET_SET)
                        .where(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                        .and(userCondition(userId))
                        .and(TARGET_SET.ID.notIn(idsToDelete))
        );

        if (remainingCount == 0) {
            throw new BadRequestException();
        }
    }
}
