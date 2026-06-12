package com.traintension.core.model.equipment;

import com.traintension.core.generated.tables.records.EquipmentRecord;
import com.traintension.core.model.equipment.EquipmentDTO.*;

import java.util.List;
import java.util.UUID;

public class EquipmentMapper {
    public static Response fromRecord(EquipmentRecord record) {
        return Response.builder()
                .id(record.getId())
                .name(record.getName())
                .description(record.getDescription())
                .mediaUrl(record.getMediaUrl())
                .createdAt(record.getCreatedAt())
                .version(record.getVersion())
                .build();
    }

    public static List<EquipmentRecord> fromDTO(CreateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    EquipmentRecord record = new EquipmentRecord();
                    record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    if (req.mediaUrl() != null) record.setMediaUrl(req.mediaUrl());
                    return record;
                })
                .toList();
    }

    public static List<EquipmentRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    EquipmentRecord record = new EquipmentRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    if (req.mediaUrl() != null) record.setMediaUrl(req.mediaUrl());
                    return record;
                })
                .toList();
    }

    public static List<EquipmentRecord> fromDTO(CreatePersonalsRequest request, UUID userId) {
        return request.items().stream()
                .map(req -> {
                    EquipmentRecord record = new EquipmentRecord();
                    record.setUserProfileId(userId);
                    record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    if (req.mediaUrl() != null) record.setMediaUrl(req.mediaUrl());
                    return record;
                })
                .toList();
    }

    public static List<EquipmentRecord> fromDTO(UpdatePersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    EquipmentRecord record = new EquipmentRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    if (req.mediaUrl() != null) record.setMediaUrl(req.mediaUrl());
                    return record;
                })
                .toList();
    }
}