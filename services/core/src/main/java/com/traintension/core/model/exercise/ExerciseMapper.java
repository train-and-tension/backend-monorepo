package com.traintension.core.model.exercise;

import com.traintension.core.generated.tables.records.ExerciseRecord;

import java.util.List;
import java.util.UUID;

import static com.traintension.core.model.exercise.ExerciseDTO.*;

public class ExerciseMapper {
    public static Response fromRecord(ExerciseRecord record) {
        return ExerciseDTO.Response.builder()
                .id(record.getId())
                .userProfileId(record.getUserProfileId())
                .equipmentId(record.getEquipmentId())
                .name(record.getName())
                .description(record.getDescription())
                .mediaUrl(record.getMediaUrl())
                .createdAt(record.getCreatedAt())
                .build();
    }


    //--------------------------------CREATE-------------------------------//
    public static List<ExerciseRecord> fromDTO(CreateGlobalsRequest request) {
        return request.items().stream()
                .map(i -> {
                    ExerciseRecord record = new ExerciseRecord();
                    record.setName(i.name());
                    record.setEquipmentId(i.equipmentId());
                    record.setDescription(i.description());
                    record.setMediaUrl(i.mediaUrl());
                    return record;
                }).toList();
    }

    public static List<ExerciseRecord> fromDTO(CreatePersonalsRequest request, UUID userId) {
        return request.items().stream()
                .map(i -> {
                    ExerciseRecord record = new ExerciseRecord();
                    record.setUserProfileId(userId);
                    record.setName(i.name());
                    record.setEquipmentId(i.equipmentId());
                    record.setDescription(i.description());
                    return record;
                }).toList();
    }


    //---------------------------------------UPDATE----------------------------------------//
    public static List<ExerciseRecord> fromDTO(UpdatePersonalsRequest request) {
        return request.items().stream()
                .map(i -> {
                    ExerciseRecord record = new ExerciseRecord();
                    record.setId(i.id());
                    if (i.name() != null) record.setName(i.name());
                    if (i.description() != null) record.setDescription(i.description());
                    if (i.equipmentId() != null) record.setEquipmentId(i.equipmentId());
                    return record;
                }).toList();
    }

    public static List<ExerciseRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(i -> {
                    ExerciseRecord record = new ExerciseRecord();
                    record.setId(i.id());
                    if (i.name() != null) record.setName(i.name());
                    if (i.description() != null) record.setDescription(i.description());
                    if (i.mediaUrl() != null) record.setMediaUrl(i.mediaUrl());
                    if (i.equipmentId() != null) record.setEquipmentId(i.equipmentId());
                    return record;
                }).toList();

    }

}
