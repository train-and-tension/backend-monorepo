package com.traintension.core.model.exercise;

import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.tables.records.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.traintension.core.model.exercise.ExerciseDTO.*;

import java.util.*;

import static com.traintension.core.generated.Tables.*;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "exercise-cache")
public class ExerciseService {
    private final DSLContext dsl;

    //---------------------------------ADMIN------------------------------------//

    @Transactional
    @CacheEvict(allEntries = true)
    public List<Response> createAll(CreateGlobalsRequest request) {
        return dsl.insertInto(EXERCISE)
                .set(ExerciseMapper.fromDTO(request))
                .returning()
                .fetch()
                .map(ExerciseMapper::fromRecord);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public List<Response> updateAll(UpdateGlobalsRequest request) {
        var updatedRecords = ExerciseMapper.fromDTO(request).stream()
                .map(r ->
                        dsl.update(EXERCISE)
                                .set(r)
                                .where(EXERCISE.ID.eq(r.getId()))
                                .and(EXERCISE.USER_PROFILE_ID.isNull())
                                .returning()
                                .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(ExerciseMapper::fromRecord).toList();
    }

    @Cacheable(key = "'id_' + #id")
    public Response findById(UUID id) {
        ExerciseRecord record = dsl.selectFrom(EXERCISE)
                .where(EXERCISE.ID.eq(id))
                .and(EXERCISE.USER_PROFILE_ID.isNull())
                .fetchOptional()
                .orElseThrow(NotFoundException::new);

        return ExerciseMapper.fromRecord(record);
    }

    @Cacheable(key = "'all'")
    public List<Response> findAll() {
        return dsl.selectFrom(EXERCISE)
                .where(EXERCISE.USER_PROFILE_ID.isNull())
                .fetch()
                .map(ExerciseMapper::fromRecord);
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<UUID> deleteByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return dsl.deleteFrom(EXERCISE)
                .where(EXERCISE.ID.in(ids))
                .and(EXERCISE.USER_PROFILE_ID.isNull())
                .returning(EXERCISE.ID)
                .fetch(EXERCISE.ID);
    }

    //------------------------------USER---------------------------------//
    @Transactional
    public List<Response> createAll(CreatePersonalsRequest request, UUID userId) {
        return dsl.insertInto(EXERCISE)
                .set(ExerciseMapper.fromDTO(request, userId))
                .returning()
                .fetch()
                .map(ExerciseMapper::fromRecord);
    }

    @Transactional
    public List<Response> updateAll(UpdatePersonalsRequest request, UUID userId) {
        var updatedRecords = ExerciseMapper.fromDTO(request).stream()
                .map(r ->
                        dsl.update(EXERCISE)
                                .set(r)
                                .where(EXERCISE.ID.eq(r.getId()))
                                .and(EXERCISE.USER_PROFILE_ID.eq(userId))
                                .returning()
                                .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(ExerciseMapper::fromRecord).toList();
    }

    @Transactional
    public List<UUID> deleteByIds(UUID userId, Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        ids.forEach(id -> validateUserExerciseCanBeDeleted(id, userId));

        List<UUID> deletedIds = dsl.deleteFrom(EXERCISE)
                .where(EXERCISE.ID.in(ids))
                .and(EXERCISE.USER_PROFILE_ID.eq(userId))
                .returning(EXERCISE.ID)
                .fetch(EXERCISE.ID);

        if (deletedIds.size() != ids.size()) {
            throw new NotFoundException();
        }
        return deletedIds;
    }

    @Transactional
    public void setFavorite(UUID exerciseId, boolean isFavorite, UUID userId) {
        boolean exerciseExists = dsl.fetchExists(
                dsl.selectOne()
                        .from(EXERCISE)
                        .where(EXERCISE.ID.eq(exerciseId))
                        .and(EXERCISE.USER_PROFILE_ID.isNull().or(EXERCISE.USER_PROFILE_ID.eq(userId)))
        );
        if (!exerciseExists) {
            throw new NotFoundException();
        }

        boolean alreadyFavorite = dsl.fetchExists(
                dsl.selectOne()
                        .from(SAVED_EXERCISE)
                        .where(SAVED_EXERCISE.USER_PROFILE_ID.eq(userId))
                        .and(SAVED_EXERCISE.EXERCISE_ID.eq(exerciseId))
        );

        if (isFavorite) {
            if (alreadyFavorite) {
                throw new BadRequestException();
            }
            int favoriteCount = dsl.fetchCount(
                    dsl.selectFrom(SAVED_EXERCISE)
                            .where(SAVED_EXERCISE.USER_PROFILE_ID.eq(userId))
            );
            if (favoriteCount >= 10) {
                throw new BadRequestException();
            }

            dsl.insertInto(SAVED_EXERCISE)
                    .set(SAVED_EXERCISE.USER_PROFILE_ID, userId)
                    .set(SAVED_EXERCISE.EXERCISE_ID, exerciseId)
                    .execute();
            return;
        }

        int deleted = dsl.deleteFrom(SAVED_EXERCISE)
                .where(SAVED_EXERCISE.USER_PROFILE_ID.eq(userId))
                .and(SAVED_EXERCISE.EXERCISE_ID.eq(exerciseId))
                .execute();
        if (deleted == 0) {
            throw new NotFoundException();
        }
    }

    private void validateUserExerciseCanBeDeleted(UUID exerciseId, UUID userId) {
        boolean exists = dsl.fetchExists(
                dsl.selectOne()
                        .from(EXERCISE)
                        .where(EXERCISE.ID.eq(exerciseId))
                        .and(EXERCISE.USER_PROFILE_ID.eq(userId))
        );
        if (!exists) {
            throw new NotFoundException();
        }

        boolean usedByTargetSet = dsl.fetchExists(
                dsl.selectOne()
                        .from(TARGET_SET)
                        .where(TARGET_SET.EXERCISE_ID.eq(exerciseId))
                        .and(TARGET_SET.USER_PROFILE_ID.eq(userId))
        );
        if (usedByTargetSet) {
            throw new BadRequestException();
        }

        boolean usedBySetResult = dsl.fetchExists(
                dsl.selectOne()
                        .from(SET_RESULT)
                        .where(SET_RESULT.EXERCISE_ID.eq(exerciseId))
                        .and(SET_RESULT.USER_PROFILE_ID.eq(userId))
        );
        if (usedBySetResult) {
            throw new BadRequestException();
        }
    }
}
