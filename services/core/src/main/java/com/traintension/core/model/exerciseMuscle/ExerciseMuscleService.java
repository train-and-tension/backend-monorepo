package com.traintension.core.model.exerciseMuscle;

import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.enums.ActivationLevel;
import com.traintension.core.generated.tables.records.ExerciseMuscleRecord;
import com.traintension.core.model.exercise.ExerciseDTO;
import com.traintension.core.model.exercise.ExerciseMapper;
import com.traintension.core.model.muscle.MuscleDTO;
import com.traintension.core.model.muscle.MuscleMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import com.traintension.core.model.exerciseMuscle.ExerciseMuscleDTO.*;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import static com.traintension.core.generated.Tables.*;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "exercise-muscle-cache")
public class ExerciseMuscleService {
    private final DSLContext dsl;
    //--------------------------------------------------------------------------------//
    //                              🛠️ADMIN METHODS🛠️
    //--------------------------------------------------------------------------------//

    @CacheEvict(allEntries = true)
    @Transactional
    public List<Response> createAll(CreateGlobalsRequest request) {
        return dsl.insertInto(EXERCISE_MUSCLE)
                .set(ExerciseMuscleMapper.fromDTO(request))
                .returning()
                .fetch()
                .map(ExerciseMuscleMapper::fromRecord);
    }


    @Cacheable(key = "'all'")
    @Transactional(readOnly = true)
    public List<Response> getAll() {
        return dsl.selectFrom(EXERCISE_MUSCLE)
                .where(EXERCISE_MUSCLE.USER_PROFILE_ID.isNull())
                .fetch()
                .map(ExerciseMuscleMapper::fromRecord);
    }

    @Cacheable(key = "'id_' + #id")
    @Transactional(readOnly = true)
    public Response getById(UUID id) {
        return dsl.selectFrom(EXERCISE_MUSCLE)
                .where(EXERCISE_MUSCLE.ID.eq(id))
                .and(EXERCISE_MUSCLE.USER_PROFILE_ID.isNull())
                .fetchOptional()
                .map(ExerciseMuscleMapper::fromRecord)
                .orElseThrow(NotFoundException::new);
    }

    @Cacheable(key = "'ex_' + #exerciseId + '_' + #activationLevel")
    @Transactional(readOnly = true)
    public MuscleResponse findMusclesByExerciseId(UUID exerciseId, ActivationLevel activationLevel) {
        var condition = EXERCISE_MUSCLE.EXERCISE_ID.eq(exerciseId)
                .and(EXERCISE_MUSCLE.USER_PROFILE_ID.isNull());

        if (activationLevel != null) {
            condition = condition.and(EXERCISE_MUSCLE.ACTIVATION_LEVEL.eq(activationLevel));
        }

        Map<ActivationLevel, List<MuscleDTO.Response>> groupedMuscles = dsl.select(
                        EXERCISE_MUSCLE.muscle().asterisk(),
                        EXERCISE_MUSCLE.ACTIVATION_LEVEL
                )
                .from(EXERCISE_MUSCLE)
                .where(condition)
                .fetchGroups(EXERCISE_MUSCLE.ACTIVATION_LEVEL,
                        r -> MuscleMapper.fromRecord(r.into(MUSCLE)));

        groupedMuscles.putIfAbsent(ActivationLevel.PRIMARY, List.of());
        groupedMuscles.putIfAbsent(ActivationLevel.SECONDARY, List.of());

        return new MuscleResponse(exerciseId, groupedMuscles);
    }

    @Cacheable(key = "'mus_' + #muscleId + '_' + #activationLevel")
    @Transactional(readOnly = true)
    public ExerciseResponse findExercisesByMuscleId(UUID muscleId, ActivationLevel activationLevel) {
        var condition = EXERCISE_MUSCLE.MUSCLE_ID.eq(muscleId)
                .and(EXERCISE_MUSCLE.USER_PROFILE_ID.isNull());

        if (activationLevel != null) {
            condition = condition.and(EXERCISE_MUSCLE.ACTIVATION_LEVEL.eq(activationLevel));
        }

        Map<ActivationLevel, List<ExerciseDTO.Response>> groupedExercises = dsl.select(
                        EXERCISE_MUSCLE.exercise().asterisk(),
                        EXERCISE_MUSCLE.ACTIVATION_LEVEL
                )
                .from(EXERCISE_MUSCLE)
                .where(condition)
                .fetchGroups(
                        EXERCISE_MUSCLE.ACTIVATION_LEVEL,
                        r -> ExerciseMapper.fromRecord(r.into(EXERCISE))
                );

        groupedExercises.putIfAbsent(ActivationLevel.PRIMARY, List.of());
        groupedExercises.putIfAbsent(ActivationLevel.SECONDARY, List.of());

        return new ExerciseResponse(muscleId, groupedExercises);
    }


    @CacheEvict(allEntries = true)
    @Transactional
    public List<UUID> deleteAll(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return dsl.deleteFrom(EXERCISE_MUSCLE)
                .where(EXERCISE_MUSCLE.ID.in(ids))
                .and(EXERCISE_MUSCLE.USER_PROFILE_ID.isNull())
                .returning(EXERCISE_MUSCLE.ID)
                .fetch(EXERCISE_MUSCLE.ID);
    }
    //--------------------------------------------------------------------------------//
    //                              🧑USER METHODS🧑️
    //--------------------------------------------------------------------------------//

    @Transactional
    public List<Response> createAll(CreatePersonalsRequest request, UUID userId) {
        return dsl.insertInto(EXERCISE_MUSCLE)
                .set(ExerciseMuscleMapper.fromDTO(request, userId))
                .returning()
                .fetch()
                .map(ExerciseMuscleMapper::fromRecord);
    }

    @Transactional
    public List<UUID> deleteAll(Set<UUID> ids, UUID userId) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return dsl.deleteFrom(EXERCISE_MUSCLE)
                .where(EXERCISE_MUSCLE.ID.in(ids))
                .and(EXERCISE_MUSCLE.USER_PROFILE_ID.eq(userId))
                .returning(EXERCISE_MUSCLE.ID)
                .fetch(EXERCISE_MUSCLE.ID);
    }
}

