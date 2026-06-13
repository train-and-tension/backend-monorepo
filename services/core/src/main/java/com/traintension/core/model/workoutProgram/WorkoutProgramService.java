package com.traintension.core.model.workoutProgram;

import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import com.traintension.core.generated.tables.records.TargetSetRecord;
import com.traintension.core.generated.tables.records.WorkoutDayRecord;
import com.traintension.core.generated.tables.records.WorkoutProgramRecord;
import com.traintension.core.model.targetSet.TargetSetMapper;
import com.traintension.core.model.workoutDay.WorkoutDayMapper;
import com.traintension.core.model.workoutPeriod.WorkoutPeriodService;
import com.traintension.core.model.workoutProgram.WorkoutProgramDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.traintension.core.generated.Tables.*;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "workout-program-cache")
public class WorkoutProgramService {
    private final DSLContext dsl;
    private final WorkoutPeriodService workoutPeriodService;

    //----------------------------------ADMIN (System)---------------------------------------//

    @Transactional
    @CacheEvict(allEntries = true)
    public List<Response> createAll(CreateGlobalsRequest request) {
        return dsl.insertInto(WORKOUT_PROGRAM)
                .set(WorkoutProgramMapper.fromDTO(request))
                .returning()
                .fetch()
                .map(WorkoutProgramMapper::fromRecord);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public List<Response> updateAll(UpdateGlobalsRequest request) {
        var updatedRecords = WorkoutProgramMapper.fromDTO(request).stream()
                .map(r ->
                        dsl.update(WORKOUT_PROGRAM)
                                .set(r)
                                .where(WORKOUT_PROGRAM.ID.eq(r.getId()))
                                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull())
                                .returning()
                                .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();


        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(WorkoutProgramMapper::fromRecord).toList();
    }

    @Cacheable(key = "'id_' + #id")
    public Response getById(UUID id) {
        return dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.eq(id))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull())
                .fetchOptional()
                .map(WorkoutProgramMapper::fromRecord)
                .orElseThrow(NotFoundException::new);
    }

    @Cacheable(key = "'all'")
    public List<Response> getAll() {
        return dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull())
                .orderBy(WORKOUT_PROGRAM.NAME.asc())
                .fetch()
                .map(WorkoutProgramMapper::fromRecord);
    }


    @Transactional
    public void deleteByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        dsl.deleteFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.in(ids))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull())
                .execute();
    }

    //----------------------------------USER (Personal)---------------------------------------//

    @Transactional
    public List<Response> createAll(CreatePersonalsRequest request, UUID userId) {
        return dsl.insertInto(WORKOUT_PROGRAM)
                .set(WorkoutProgramMapper.fromDTO(request, userId))
                .returning()
                .fetch()
                .map(WorkoutProgramMapper::fromRecord);
    }

    @Transactional
    public List<Response> updateAll(UpdatePersonalsRequest request, UUID userId) {
        var updatedRecords = WorkoutProgramMapper.fromDTO(request).stream()
                .map(r ->
                        dsl.update(WORKOUT_PROGRAM)
                                .set(r)
                                .where(WORKOUT_PROGRAM.ID.eq(r.getId()))
                                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                                .returning()
                                .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(WorkoutProgramMapper::fromRecord).toList();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteByIds(UUID userId, Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (UUID id : ids) {
            deletePersonalWorkoutProgram(userId, id);
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deactivateWorkoutProgram(UUID id, UUID userId) {
        int updated = dsl.update(WORKOUT_PROGRAM)
                .set(WORKOUT_PROGRAM.IS_ACTIVE, Boolean.FALSE)
                .where(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_PROGRAM.ID.eq(id))
                .and(WORKOUT_PROGRAM.IS_ACTIVE.isTrue())
                .execute();

        if (updated == 0) {
            throw new NotFoundException();
        }

        UUID workoutPeriodId = dsl.select(WORKOUT_PERIOD.ID)
                .from(WORKOUT_PROGRAM)
                .join(WORKOUT_PERIOD).on(WORKOUT_PERIOD.WORKOUT_PROGRAM_ID.eq(id))
                .where(WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_PERIOD.IS_ACTIVE.isTrue())
                .fetchAnyInto(UUID.class);

        workoutPeriodService.endOldPeriod(workoutPeriodId, userId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void activateWorkoutProgram(UUID id, LocalDate startDate, UUID userId) {
        checkDate(startDate, userId);

        WorkoutProgramRecord sourceProgram = findActivatableProgram(id, userId);
        WorkoutProgramRecord activeProgram = findActiveWorkoutProgram(userId);
        if (activeProgram != null && activeProgram.getId().equals(sourceProgram.getId())) {
            throw new BadRequestException();
        }

        validateProgramHasFirstDayWithTargetSet(sourceProgram.getId(), sourceProgram.getUserProfileId());

        WorkoutProgramRecord programToActivate = sourceProgram.getUserProfileId() == null
                ? duplicateWorkoutProgramRecordForUser(sourceProgram.getId(), userId)
                : sourceProgram;

        deactivateCurrentProgram(userId);

        int updated = dsl.update(WORKOUT_PROGRAM)
                .set(WORKOUT_PROGRAM.IS_ACTIVE, Boolean.TRUE)
                .where(WORKOUT_PROGRAM.ID.eq(programToActivate.getId()))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .execute();

        if (updated == 0) {
            throw new NotFoundException();
        }

        WorkoutDayRecord firstDay = findFirstWorkoutDay(programToActivate.getId(), userId);
        int exerciseCount = countTargetSets(firstDay.getId(), userId);
        UUID workoutPeriodId = createWorkoutPeriod(programToActivate, startDate, userId);
        createPlannedWorkoutSession(firstDay, workoutPeriodId, startDate, exerciseCount, userId);
    }

    private WorkoutProgramRecord findActivatableProgram(UUID id, UUID userId) {
        return dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.eq(id))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull()
                        .or(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId)))
                .fetchOptional()
                .orElseThrow(NotFoundException::new);
    }

    private WorkoutProgramRecord findActiveWorkoutProgram(UUID userId) {
        return dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.IS_ACTIVE.isTrue())
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .fetchOne();
    }

    private UUID findActiveWorkoutPeriodId(UUID userId) {
        return dsl.select(WORKOUT_PERIOD.ID)
                .from(WORKOUT_PERIOD)
                .where(WORKOUT_PERIOD.IS_ACTIVE.isTrue())
                .and(WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId))
                .fetchOneInto(UUID.class);
    }

    private void deactivateCurrentProgram(UUID userId) {
        UUID activeWorkoutPeriodId = findActiveWorkoutPeriodId(userId);

        if (activeWorkoutPeriodId != null) {
            workoutPeriodService.endOldPeriod(activeWorkoutPeriodId, userId);
        }

        dsl.update(WORKOUT_PROGRAM)
                .set(WORKOUT_PROGRAM.IS_ACTIVE, Boolean.FALSE)
                .where(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_PROGRAM.IS_ACTIVE.isTrue())
                .execute();
    }

    private void checkDate(LocalDate startDate, UUID userId) {
        if (startDate == null || !startDate.isAfter(LocalDate.now(resolveUserZone(userId)))) {
            throw new BadRequestException();
        }
    }

    private void validateProgramHasFirstDayWithTargetSet(UUID workoutProgramId, UUID ownerUserId) {
        WorkoutDayRecord firstDay = findFirstWorkoutDay(workoutProgramId, ownerUserId);
        if (firstDay == null || countTargetSets(firstDay.getId(), ownerUserId) == 0) {
            throw new BadRequestException();
        }
    }

    private WorkoutDayRecord findFirstWorkoutDay(UUID workoutProgramId, UUID ownerUserId) {
        return dsl.selectFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(workoutDayOwnerCondition(ownerUserId))
                .orderBy(WORKOUT_DAY.ORDER_NUMBER.asc())
                .limit(1)
                .fetchOne();
    }

    private int countTargetSets(UUID workoutDayId, UUID ownerUserId) {
        return dsl.fetchCount(
                dsl.selectFrom(TARGET_SET)
                        .where(TARGET_SET.WORKOUT_DAY_ID.eq(workoutDayId))
                        .and(targetSetOwnerCondition(ownerUserId))
        );
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public DuplicateResponse duplicateWorkoutProgramForUser(UUID sourceWorkoutProgramId, UUID userId) {
        DuplicateCopy duplicated = duplicateWorkoutProgramGraphForUser(sourceWorkoutProgramId, userId);
        Response duplicatedProgram = WorkoutProgramMapper.fromRecord(duplicated.program());
        return DuplicateResponse.builder()
                .id(duplicatedProgram.id())
                .userProfileId(duplicatedProgram.userProfileId())
                .name(duplicatedProgram.name())
                .description(duplicatedProgram.description())
                .isActive(duplicatedProgram.isActive())
                .createdAt(duplicatedProgram.createdAt())
                .updatedAt(duplicatedProgram.updatedAt())
                .workoutProgram(duplicatedProgram)
                .workoutDays(WorkoutDayMapper.fromRecord(duplicated.days(), duplicated.program().getId()))
                .targetSets(TargetSetMapper.fromRecord(
                        duplicated.program().getId(),
                        duplicated.targetSets().stream().collect(java.util.stream.Collectors.groupingBy(TargetSetRecord::getWorkoutDayId))
                ))
                .build();
    }

    private WorkoutProgramRecord duplicateWorkoutProgramRecordForUser(UUID sourceWorkoutProgramId, UUID userId) {
        return duplicateWorkoutProgramGraphForUser(sourceWorkoutProgramId, userId).program();
    }

    private DuplicateCopy duplicateWorkoutProgramGraphForUser(UUID sourceWorkoutProgramId, UUID userId) {
        WorkoutProgramRecord sourceProgram = dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.eq(sourceWorkoutProgramId))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.isNull())
                .fetchOptional()
                .orElseThrow(NotFoundException::new);

        WorkoutProgramRecord duplicatedProgram = dsl.insertInto(WORKOUT_PROGRAM)
                .set(WORKOUT_PROGRAM.USER_PROFILE_ID, userId)
                .set(WORKOUT_PROGRAM.IS_EDITED, Boolean.FALSE)
                .set(WORKOUT_PROGRAM.IS_ACTIVE, Boolean.FALSE)
                .set(WORKOUT_PROGRAM.NAME, sourceProgram.getName())
                .set(WORKOUT_PROGRAM.DESCRIPTION, sourceProgram.getDescription())
                .returning()
                .fetchOne();

        if (duplicatedProgram == null) {
            throw new NotFoundException();
        }

        DuplicatedChildren children = duplicateWorkoutDaysAndTargetSets(
                sourceProgram.getId(),
                sourceProgram.getUserProfileId(),
                duplicatedProgram.getId(),
                userId
        );
        return new DuplicateCopy(duplicatedProgram, children.days(), children.targetSets());
    }

    private DuplicatedChildren duplicateWorkoutDaysAndTargetSets(
            UUID sourceWorkoutProgramId,
            UUID sourceOwnerUserId,
            UUID duplicatedWorkoutProgramId,
            UUID userId
    ) {
        List<WorkoutDayRecord> sourceDays = dsl.selectFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(sourceWorkoutProgramId))
                .and(workoutDayOwnerCondition(sourceOwnerUserId))
                .orderBy(WORKOUT_DAY.ORDER_NUMBER.asc())
                .fetch();

        Map<UUID, UUID> dayIdMap = new HashMap<>();
        List<WorkoutDayRecord> duplicatedDays = new ArrayList<>();
        for (WorkoutDayRecord sourceDay : sourceDays) {
            WorkoutDayRecord duplicatedDay = dsl.insertInto(WORKOUT_DAY)
                    .set(WORKOUT_DAY.USER_PROFILE_ID, userId)
                    .set(WORKOUT_DAY.WORKOUT_PROGRAM_ID, duplicatedWorkoutProgramId)
                    .set(WORKOUT_DAY.IS_OFF, sourceDay.getIsOff())
                    .set(WORKOUT_DAY.ORDER_NUMBER, sourceDay.getOrderNumber())
                    .set(WORKOUT_DAY.NAME, sourceDay.getName())
                    .returning()
                    .fetchOne();

            if (duplicatedDay == null) {
                throw new NotFoundException();
            }

            dayIdMap.put(sourceDay.getId(), duplicatedDay.getId());
            duplicatedDays.add(duplicatedDay);
        }

        List<TargetSetRecord> duplicatedTargetSets = new ArrayList<>();
        for (WorkoutDayRecord sourceDay : sourceDays) {
            List<TargetSetRecord> sourceTargetSets = dsl.selectFrom(TARGET_SET)
                    .where(TARGET_SET.WORKOUT_DAY_ID.eq(sourceDay.getId()))
                    .and(targetSetOwnerCondition(sourceOwnerUserId))
                    .orderBy(TARGET_SET.ORDER_NUMBER.asc())
                    .fetch();

            for (TargetSetRecord sourceTargetSet : sourceTargetSets) {
                TargetSetRecord duplicatedTargetSet = dsl.insertInto(TARGET_SET)
                        .set(TARGET_SET.USER_PROFILE_ID, userId)
                        .set(TARGET_SET.EXERCISE_ID, sourceTargetSet.getExerciseId())
                        .set(TARGET_SET.WORKOUT_DAY_ID, dayIdMap.get(sourceDay.getId()))
                        .set(TARGET_SET.WEIGHT_KG, sourceTargetSet.getWeightKg())
                        .set(TARGET_SET.DURATION, sourceTargetSet.getDuration())
                        .set(TARGET_SET.UNIT, sourceTargetSet.getUnit())
                        .set(TARGET_SET.REST_DURATION, sourceTargetSet.getRestDuration())
                        .set(TARGET_SET.REP_COUNT, sourceTargetSet.getRepCount())
                        .set(TARGET_SET.ORDER_NUMBER, sourceTargetSet.getOrderNumber())
                        .returning()
                        .fetchOne();

                if (duplicatedTargetSet == null) {
                    throw new NotFoundException();
                }

                duplicatedTargetSets.add(duplicatedTargetSet);
            }
        }

        return new DuplicatedChildren(duplicatedDays, duplicatedTargetSets);
    }

    private record DuplicateCopy(
            WorkoutProgramRecord program,
            List<WorkoutDayRecord> days,
            List<TargetSetRecord> targetSets
    ) {}

    private record DuplicatedChildren(
            List<WorkoutDayRecord> days,
            List<TargetSetRecord> targetSets
    ) {}

    private UUID createWorkoutPeriod(WorkoutProgramRecord program, LocalDate startDate, UUID userId) {
        UUID workoutPeriodId = dsl.insertInto(WORKOUT_PERIOD)
                .set(WORKOUT_PERIOD.USER_PROFILE_ID, userId)
                .set(WORKOUT_PERIOD.WORKOUT_PROGRAM_ID, program.getId())
                .set(WORKOUT_PERIOD.IS_ACTIVE, Boolean.TRUE)
                .set(WORKOUT_PERIOD.START_DATE, toUserStartOfDay(startDate, userId))
                .set(WORKOUT_PERIOD.WORKOUT_PROGRAM_NAME_SNAPSHOT, program.getName())
                .returning(WORKOUT_PERIOD.ID)
                .fetchOne(WORKOUT_PERIOD.ID);

        if (workoutPeriodId == null) {
            throw new NotFoundException();
        }

        return workoutPeriodId;
    }

    private void createPlannedWorkoutSession(
            WorkoutDayRecord firstDay,
            UUID workoutPeriodId,
            LocalDate startDate,
            int exerciseCount,
            UUID userId
    ) {
        dsl.insertInto(WORKOUT_SESSION)
                .set(WORKOUT_SESSION.USER_PROFILE_ID, userId)
                .set(WORKOUT_SESSION.WORKOUT_PERIOD_ID, workoutPeriodId)
                .set(WORKOUT_SESSION.WORKOUT_DAY_ID, firstDay.getId())
                .set(WORKOUT_SESSION.START_DATE, startDate)
                .set(WORKOUT_SESSION.STATUS, WorkoutStatus.PLANNED)
                .set(WORKOUT_SESSION.TYPE, Boolean.TRUE.equals(firstDay.getIsOff()) ? SessionType.OFF : SessionType.WORKOUT)
                .set(WORKOUT_SESSION.WORKOUT_DAY_NAME_SNAPSHOT, firstDay.getName())
                .set(WORKOUT_SESSION.EXERCISE_COUNT_SNAPSHOT, exerciseCount)
                .execute();
    }

    private OffsetDateTime toUserStartOfDay(LocalDate startDate, UUID userId) {
        return startDate.atStartOfDay(resolveUserZone(userId)).toOffsetDateTime();
    }

    private ZoneId resolveUserZone(UUID userId) {
        String timezone = dsl.select(USER_PROFILE.TIMEZONE)
                .from(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(userId))
                .fetchOneInto(String.class);

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezone != null ? timezone : "UTC");
        } catch (Exception e) {
            zoneId = ZoneId.of("UTC");
        }

        return zoneId;
    }

    private Condition workoutDayOwnerCondition(UUID ownerUserId) {
        return ownerUserId == null
                ? WORKOUT_DAY.USER_PROFILE_ID.isNull()
                : WORKOUT_DAY.USER_PROFILE_ID.eq(ownerUserId);
    }

    private Condition targetSetOwnerCondition(UUID ownerUserId) {
        return ownerUserId == null
                ? TARGET_SET.USER_PROFILE_ID.isNull()
                : TARGET_SET.USER_PROFILE_ID.eq(ownerUserId);
    }

    private void deletePersonalWorkoutProgram(UUID userId, UUID workoutProgramId) {
        WorkoutProgramRecord program = dsl.selectFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.eq(workoutProgramId))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .fetchOptional()
                .orElseThrow(NotFoundException::new);

        if (Boolean.TRUE.equals(program.getIsActive())) {
            deactivateWorkoutProgram(workoutProgramId, userId);
        }

        if (hasFinishedWorkoutHistory(userId, workoutProgramId)) {
            dsl.update(WORKOUT_PROGRAM)
                    .set(WORKOUT_PROGRAM.IS_ACTIVE, Boolean.FALSE)
                    .where(WORKOUT_PROGRAM.ID.eq(workoutProgramId))
                    .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                    .execute();
            return;
        }

        dsl.deleteFrom(TARGET_SET)
                .where(TARGET_SET.WORKOUT_DAY_ID.in(
                        dsl.select(WORKOUT_DAY.ID)
                                .from(WORKOUT_DAY)
                                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                                .and(WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                ))
                .and(TARGET_SET.USER_PROFILE_ID.eq(userId))
                .execute();

        dsl.deleteFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .execute();

        int deleted = dsl.deleteFrom(WORKOUT_PROGRAM)
                .where(WORKOUT_PROGRAM.ID.eq(workoutProgramId))
                .and(WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
                .execute();

        if (deleted == 0) {
            throw new NotFoundException();
        }
    }

    private boolean hasFinishedWorkoutHistory(UUID userId, UUID workoutProgramId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(WORKOUT_SESSION)
                        .join(WORKOUT_PERIOD).on(WORKOUT_PERIOD.ID.eq(WORKOUT_SESSION.WORKOUT_PERIOD_ID))
                        .where(WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId))
                        .and(WORKOUT_PERIOD.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                        .and(WORKOUT_SESSION.STATUS.eq(WorkoutStatus.FINISHED))
                        .and(WORKOUT_SESSION.TYPE.eq(SessionType.WORKOUT))
        );
    }
}
