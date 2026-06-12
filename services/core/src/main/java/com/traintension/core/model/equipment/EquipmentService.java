package com.traintension.core.model.equipment;

import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.model.equipment.EquipmentDTO.*;
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

import static com.traintension.core.generated.Tables.EQUIPMENT;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "equipments-cache")
public class EquipmentService {

    private final DSLContext dsl;

    //--------------------------------------------------------------------------------//
    //                              🛠️ADMIN METHODS🛠️
    //--------------------------------------------------------------------------------//

    @Cacheable(key = "'id_' + #id")
    @Transactional(readOnly = true)
    public Response findById(UUID id) {
        return dsl.selectFrom(EQUIPMENT)
                .where(EQUIPMENT.ID.eq(id))
                .and(EQUIPMENT.USER_PROFILE_ID.isNull())
                .fetchOptional()
                .map(EquipmentMapper::fromRecord)
                .orElseThrow(NotFoundException::new);
    }

    @Cacheable(key = "'all'")
    @Transactional(readOnly = true)
    public List<Response> findAll() {
        return dsl.selectFrom(EQUIPMENT)
                .where(EQUIPMENT.USER_PROFILE_ID.isNull())
                .orderBy(EQUIPMENT.NAME.asc())
                .fetch(EquipmentMapper::fromRecord);
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<Response> createAll(CreateGlobalsRequest request) {
        return dsl.insertInto(EQUIPMENT)
                .set(EquipmentMapper.fromDTO(request))
                .returning()
                .fetch()
                .map(EquipmentMapper::fromRecord);
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<Response> updateAll(UpdateGlobalsRequest request) {
        var updatedRecords = EquipmentMapper.fromDTO(request).stream()
                .map(r -> dsl.update(EQUIPMENT)
                        .set(r)
                        .where(EQUIPMENT.ID.eq(r.getId()))
                        .and(EQUIPMENT.USER_PROFILE_ID.isNull())
                        .returning()
                        .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(EquipmentMapper::fromRecord).toList();
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public List<UUID> deleteEquipments(Set<UUID> ids) {
        return dsl.deleteFrom(EQUIPMENT)
                .where(EQUIPMENT.ID.in(ids))
                .and(EQUIPMENT.USER_PROFILE_ID.isNull())
                .returning(EQUIPMENT.ID)
                .fetch(EQUIPMENT.ID);
    }

    //--------------------------------------------------------------------------------//
    //                              🧑USER METHODS🧑
    //--------------------------------------------------------------------------------//

    @Transactional
    public List<Response> createAll(CreatePersonalsRequest request, UUID userId) {
        return dsl.insertInto(EQUIPMENT)
                .set(EquipmentMapper.fromDTO(request, userId))
                .returning()
                .fetch()
                .map(EquipmentMapper::fromRecord);
    }

    @Transactional
    public List<Response> updateAll(UpdatePersonalsRequest request, UUID userId) {
        var updatedRecords = EquipmentMapper.fromDTO(request).stream()
                .map(r -> dsl.update(EQUIPMENT)
                        .set(r)
                        .where(EQUIPMENT.ID.eq(r.getId()))
                        .and(EQUIPMENT.USER_PROFILE_ID.eq(userId))
                        .returning()
                        .fetchOne()
                )
                .filter(Objects::nonNull)
                .toList();

        if (updatedRecords.size() != request.items().size()) {
            throw new NotFoundException();
        }

        return updatedRecords.stream().map(EquipmentMapper::fromRecord).toList();
    }


    @Transactional
    public List<UUID> deleteEquipments(Set<UUID> ids, UUID userId) {
        return dsl.deleteFrom(EQUIPMENT)
                .where(EQUIPMENT.ID.in(ids))
                .and(EQUIPMENT.USER_PROFILE_ID.eq(userId))
                .returning(EQUIPMENT.ID)
                .fetch(EQUIPMENT.ID);
    }


}