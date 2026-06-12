package com.traintension.core.model.muscle;

import com.traintension.common.exception.custom.ConflictException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.common.exception.custom.SecureException;
import com.traintension.core.generated.tables.records.MuscleRecord;
import com.traintension.core.model.muscle.MuscleDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import static com.traintension.core.generated.Tables.MUSCLE;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "muscles-cache")
public class MuscleService {
    private final DSLContext dsl;

    @CacheEvict(allEntries = true)
    @Transactional
    public List<Response> createAll(CreateGlobalsRequest request) {
        return dsl.insertInto(MUSCLE)
                .set(MuscleMapper.fromDTO(request))
                .returning()
                .fetch()
                .map(MuscleMapper::fromRecord);
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<Response> updateAll(UpdateGlobalsRequest request) {
        var updatedRecords = MuscleMapper.fromDTO(request).stream().map(r ->
                        dsl.update(MUSCLE)
                                .set(r)
                                .where(MUSCLE.ID.eq(r.getId()))
                                .returning()
                                .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(MuscleMapper::fromRecord).toList();
    }

    @Cacheable(key = "'all'")
    public List<Response> getAll() {
        return dsl.selectFrom(MUSCLE)
                .orderBy(MUSCLE.NAME.asc())
                .fetch()
                .stream()
                .map(MuscleMapper::fromRecord)
                .toList();
    }

    @Cacheable(key = "'id_' + #id")
    public Response getById(UUID id) {
        return dsl.selectFrom(MUSCLE)
                .where(MUSCLE.ID.eq(id))
                .fetchOptional()
                .map(MuscleMapper::fromRecord)
                .orElseThrow(NotFoundException::new);
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<UUID> deleteByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return dsl.deleteFrom(MUSCLE)
                .where(MUSCLE.ID.in(ids))
                .returning(MUSCLE.ID)
                .fetch(MUSCLE.ID);
    }
}
