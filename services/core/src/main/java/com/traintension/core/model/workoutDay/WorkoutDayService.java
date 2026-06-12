package com.traintension.core.model.workoutDay;

import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.generated.tables.records.WorkoutDayRecord;
import com.traintension.core.generated.tables.records.WorkoutSessionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.traintension.core.model.workoutDay.WorkoutDayDTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.traintension.core.generated.Tables.*;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "workout-days-cache")
public class WorkoutDayService {
    private final DSLContext dsl;


    //--------------------------------------------------------------------------------//
    //                              🛠️ADMIN METHODS🛠️
    //--------------------------------------------------------------------------------//

    @Transactional
    @CacheEvict(allEntries = true)
    public Responses createGlobalWorkoutDays(CreateGlobalsRequest request, UUID workoutProgramId) {
        validateWorkoutProgram(workoutProgramId, null);
        int maxOrder = getMaxWorkoutDayOrder(workoutProgramId, null);

        List<WorkoutDayRecord> insertedRecords = dsl.insertInto(WORKOUT_DAY)
                .set(WorkoutDayMapper.fromDTO(request, workoutProgramId, maxOrder))
                .returning()
                .fetch();

        return WorkoutDayMapper.fromRecord(insertedRecords, workoutProgramId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Responses reorderGlobalWorkoutDays(ReorderGlobalsRequest request, UUID workoutProgramId) {
        validateWorkoutDays(workoutProgramId, request.items(), null);
        dsl.batchUpdate(WorkoutDayMapper.fromDTO(request)).execute();

        List<WorkoutDayRecord> allCurrentWorkoutDays = getWorkoutDays(workoutProgramId, null);
        return WorkoutDayMapper.fromRecord(allCurrentWorkoutDays, workoutProgramId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Responses updateGlobalWorkoutDays(UpdateGlobalsRequest request, UUID workoutProgramId) {
        validateWorkoutDays(workoutProgramId, request.items(), null);
        dsl.batchUpdate(WorkoutDayMapper.fromDTO(request)).execute();

        List<WorkoutDayRecord> updatedWorkoutDays = getWorkoutDaysByIdIn(
                workoutProgramId,
                getRequestedIds(request.items()),
                null
        );

        return WorkoutDayMapper.fromRecord(updatedWorkoutDays, workoutProgramId);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'global:' + #workoutProgramId")
    public Responses getAllGlobalDaysByProgram(UUID workoutProgramId) {
        return WorkoutDayMapper.fromRecord(getWorkoutDays(workoutProgramId, null), workoutProgramId);
    }

    @Transactional(readOnly = true)
    public List<Response> getWorkoutsDayByIds(Set<UUID> workoutDayIds) {
        return dsl.selectFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.ID.in(workoutDayIds))
                .and(WORKOUT_DAY.USER_PROFILE_ID.isNull())
                .fetch(WorkoutDayMapper::fromRecord);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public List<UUID> deleteGlobalWorkoutDays(UUID workoutProgramId, Set<UUID> workoutDayIds) {
        return deleteByIdsIn(workoutProgramId, workoutDayIds, null);
    }

    //--------------------------------------------------------------------------------//
    //                              🧑USER METHODS🧑
    //--------------------------------------------------------------------------------//

    @Transactional
    public Responses createPersonalWorkoutDays(CreatePersonalsRequest request, UUID workoutProgramId, UUID userId) {
        validateWorkoutProgram(workoutProgramId, userId);
        int maxOrder = getMaxWorkoutDayOrder(workoutProgramId, userId);

        List<WorkoutDayRecord> insertedRecords = dsl.insertInto(WORKOUT_DAY)
                .set(WorkoutDayMapper.fromDTO(request, workoutProgramId, maxOrder, userId))
                .returning()
                .fetch();

        return WorkoutDayMapper.fromRecord(insertedRecords, workoutProgramId);
    }

    @Transactional
    public Responses reorderPersonalWorkoutDays(ReorderPersonalsRequest request, UUID workoutProgramId, UUID userId) {
        validateWorkoutDays(workoutProgramId, request.items(), userId);
        Map<UUID, WorkoutDayRecord> daysBeforeUpdate = getWorkoutDaysByIdIn(
                workoutProgramId,
                getRequestedIds(request.items()),
                userId
        ).stream().collect(java.util.stream.Collectors.toMap(WorkoutDayRecord::getId, d -> d));
        Map<UUID, List<WorkoutSessionRecord>> plannedSessionsByDay = getPlannedSessionsByDay(daysBeforeUpdate.keySet(), userId);

        dsl.batchUpdate(WorkoutDayMapper.fromDTO(request)).execute();

        List<WorkoutDayRecord> allCurrentWorkoutDays = getWorkoutDays(workoutProgramId, userId);
        relinkPlannedSessionsAfterReorder(daysBeforeUpdate, plannedSessionsByDay, allCurrentWorkoutDays);
        return WorkoutDayMapper.fromRecord(allCurrentWorkoutDays, workoutProgramId);
    }

    @Transactional
    public Responses updatePersonalWorkoutDays(UpdatePersonalsRequest request, UUID workoutProgramId, UUID userId) {
        validateWorkoutDays(workoutProgramId, request.items(), userId);
        dsl.batchUpdate(WorkoutDayMapper.fromDTO(request)).execute();

        List<WorkoutDayRecord> updatedWorkoutDays = getWorkoutDaysByIdIn(
                workoutProgramId,
                getRequestedIds(request.items()),
                userId
        );

        syncPlannedSessionsWithWorkoutDays(updatedWorkoutDays, userId);
        return WorkoutDayMapper.fromRecord(updatedWorkoutDays, workoutProgramId);
    }

    @Transactional
    public List<UUID> deletePersonalWorkoutDays(UUID workoutProgramId, Set<UUID> workoutDayIds, UUID userId) {
        if (workoutDayIds == null || workoutDayIds.isEmpty()) {
            return List.of();
        }

        List<WorkoutDayRecord> deletedDays = getWorkoutDaysByIdIn(workoutProgramId, workoutDayIds.stream().toList(), userId);
        if (deletedDays.size() != workoutDayIds.size()) {
            throw new NotFoundException("One or more workout days not found or do not belong to this program");
        }

        int remainingWorkoutDays = dsl.fetchCount(
                dsl.selectFrom(WORKOUT_DAY)
                        .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                        .and(WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                        .and(WORKOUT_DAY.ID.notIn(workoutDayIds))
                        .and(WORKOUT_DAY.IS_OFF.isFalse())
        );
        if (remainingWorkoutDays == 0) {
            throw new BadRequestException();
        }

        Map<UUID, List<WorkoutSessionRecord>> plannedSessionsByDay = getPlannedSessionsByDay(workoutDayIds, userId);
        List<UUID> deletedIds = deleteByIdsIn(workoutProgramId, workoutDayIds, userId);
        List<WorkoutDayRecord> remainingDays = getWorkoutDays(workoutProgramId, userId);
        relinkPlannedSessionsAfterDelete(deletedDays, plannedSessionsByDay, remainingDays);
        return deletedIds;
    }


    //--------------------------------------------------------------------------------//
    //                              🔧PRIVATE METHODS🔧
    //--------------------------------------------------------------------------------//
    private List<WorkoutDayRecord> getWorkoutDaysByIdIn(
            UUID workoutProgramId,
            List<UUID> workoutDayIds,
            UUID userId
    ) {
        return dsl.selectFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(WORKOUT_DAY.ID.in(workoutDayIds))
                .and(userId == null ? WORKOUT_DAY.USER_PROFILE_ID.isNull() : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .fetch();
    }

    private List<WorkoutDayRecord> getWorkoutDays(UUID workoutProgramId, UUID userId) {
        return dsl.selectFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(userId == null
                        ? WORKOUT_DAY.USER_PROFILE_ID.isNull()
                        : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .fetch();
    }

    //IDsi verilen Workout Day'lerin hepsinin varlığını sorgulayıp, doğrular.
    private void validateWorkoutDays(UUID workoutProgramId, List<? extends HasId> items, UUID userId) {
        List<UUID> requestedIds = getRequestedIds(items);

        boolean allExists = dsl.fetchCount(
                dsl.selectFrom(WORKOUT_DAY)
                        .where(WORKOUT_DAY.ID.in(requestedIds))
                        .and(userId == null ? WORKOUT_DAY.USER_PROFILE_ID.isNull() : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                        .and(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
        ) == requestedIds.size();

        if (!allExists) {
            throw new NotFoundException("One or more workout days not found or do not belong to this program");
        }
    }


    //Workout Program Varlığının Sorgular ve Doğrular
    private void validateWorkoutProgram(UUID workoutProgramId, UUID userId) {
        boolean programExists = dsl.fetchExists(
                dsl.selectOne().from(WORKOUT_PROGRAM)
                        .where(WORKOUT_PROGRAM.ID.eq(workoutProgramId))
                        .and(userId == null ? WORKOUT_PROGRAM.USER_PROFILE_ID.isNull() : WORKOUT_PROGRAM.USER_PROFILE_ID.eq(userId))
        );

        if (!programExists) {
            throw new NotFoundException("Workout Program Not Found");
        }
    }

    //Workout Program'a bağlı Workout Daylerin en büyük sırasını alır.
    private Integer getMaxWorkoutDayOrder(UUID workoutProgramId, UUID userId) {
        Integer max = dsl.select(DSL.max(WORKOUT_DAY.ORDER_NUMBER))
                .from(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(userId == null
                        ? WORKOUT_DAY.USER_PROFILE_ID.isNull()
                        : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .fetchOneInto(Integer.class);

        return max == null ? 0 : max;
    }


    //Verilen İstek Listesindeki Nesnelerin ID listesini döndürür.
    private List<UUID> getRequestedIds(List<? extends HasId> items) {
        return items.stream()
                .map(HasId::id)
                .toList();
    }


    private List<UUID> deleteByIdsIn(UUID workoutProgramId, Set<UUID> workoutDayIds, UUID userId) {
        return dsl.deleteFrom(WORKOUT_DAY)
                .where(WORKOUT_DAY.ID.in(workoutDayIds))
                .and(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(userId == null
                        ? WORKOUT_DAY.USER_PROFILE_ID.isNull()
                        : WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .returning(WORKOUT_DAY.ID)
                .fetch(WORKOUT_DAY.ID);
    }

    private Map<UUID, List<WorkoutSessionRecord>> getPlannedSessionsByDay(Collection<UUID> workoutDayIds, UUID userId) {
        if (workoutDayIds == null || workoutDayIds.isEmpty()) {
            return Map.of();
        }

        return dsl.selectFrom(WORKOUT_SESSION)
                .where(WORKOUT_SESSION.WORKOUT_DAY_ID.in(workoutDayIds))
                .and(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_SESSION.STATUS.eq(WorkoutStatus.PLANNED))
                .fetch()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(WorkoutSessionRecord::getWorkoutDayId));
    }

    private void syncPlannedSessionsWithWorkoutDays(List<WorkoutDayRecord> days, UUID userId) {
        for (WorkoutDayRecord day : days) {
            dsl.update(WORKOUT_SESSION)
                    .set(WORKOUT_SESSION.WORKOUT_DAY_NAME_SNAPSHOT, day.getName())
                    .set(WORKOUT_SESSION.TYPE, Boolean.TRUE.equals(day.getIsOff()) ? SessionType.OFF : SessionType.WORKOUT)
                    .where(WORKOUT_SESSION.WORKOUT_DAY_ID.eq(day.getId()))
                    .and(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
                    .and(WORKOUT_SESSION.STATUS.eq(WorkoutStatus.PLANNED))
                    .execute();
        }
    }

    private void relinkPlannedSessionsAfterReorder(
            Map<UUID, WorkoutDayRecord> daysBeforeUpdate,
            Map<UUID, List<WorkoutSessionRecord>> plannedSessionsByDay,
            List<WorkoutDayRecord> allCurrentWorkoutDays
    ) {
        for (var entry : plannedSessionsByDay.entrySet()) {
            WorkoutDayRecord previousDay = daysBeforeUpdate.get(entry.getKey());
            if (previousDay == null) {
                continue;
            }

            WorkoutDayRecord newDay = allCurrentWorkoutDays.stream()
                    .filter(day -> Objects.equals(day.getOrderNumber(), previousDay.getOrderNumber()))
                    .findFirst()
                    .orElse(previousDay);
            updatePlannedSessions(entry.getValue(), newDay);
        }
    }

    private void relinkPlannedSessionsAfterDelete(
            List<WorkoutDayRecord> deletedDays,
            Map<UUID, List<WorkoutSessionRecord>> plannedSessionsByDay,
            List<WorkoutDayRecord> remainingDays
    ) {
        for (WorkoutDayRecord deletedDay : deletedDays) {
            List<WorkoutSessionRecord> sessions = plannedSessionsByDay.getOrDefault(deletedDay.getId(), List.of());
            if (sessions.isEmpty()) {
                continue;
            }

            WorkoutDayRecord replacement = remainingDays.stream()
                    .filter(day -> day.getOrderNumber() > deletedDay.getOrderNumber())
                    .min(Comparator.comparing(WorkoutDayRecord::getOrderNumber))
                    .orElseGet(() -> remainingDays.stream()
                            .min(Comparator.comparing(WorkoutDayRecord::getOrderNumber))
                            .orElseThrow(NotFoundException::new));

            updatePlannedSessions(sessions, replacement);
        }
    }

    private void updatePlannedSessions(List<WorkoutSessionRecord> sessions, WorkoutDayRecord day) {
        for (WorkoutSessionRecord session : sessions) {
            dsl.update(WORKOUT_SESSION)
                    .set(WORKOUT_SESSION.WORKOUT_DAY_ID, day.getId())
                    .set(WORKOUT_SESSION.WORKOUT_DAY_NAME_SNAPSHOT, day.getName())
                    .set(WORKOUT_SESSION.TYPE, Boolean.TRUE.equals(day.getIsOff()) ? SessionType.OFF : SessionType.WORKOUT)
                    .where(WORKOUT_SESSION.ID.eq(session.getId()))
                    .execute();
        }
    }
}
