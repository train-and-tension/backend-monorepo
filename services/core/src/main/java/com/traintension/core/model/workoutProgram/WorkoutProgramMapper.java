package com.traintension.core.model.workoutProgram;

import com.traintension.core.generated.tables.records.WorkoutProgramRecord;
import com.traintension.core.model.workoutProgram.WorkoutProgramDTO.*;

import java.util.List;
import java.util.UUID;

public class WorkoutProgramMapper {

    public static Response fromRecord(WorkoutProgramRecord record) {
        return Response.builder()
                .id(record.getId())
                .userProfileId(record.getUserProfileId())
                .name(record.getName())
                .description(record.getDescription())
                .isActive(record.getIsActive())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    public static List<WorkoutProgramRecord> fromDTO(CreateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutProgramRecord record = new WorkoutProgramRecord();
                    record.setName(req.name());
                    record.setDescription(req.description());
                    record.setIsActive(false);
                    return record;
                }).toList();
    }

    public static List<WorkoutProgramRecord> fromDTO(CreatePersonalsRequest request, UUID userId) {
        return request.items().stream()
                .map(req -> {
                    WorkoutProgramRecord record = new WorkoutProgramRecord();
                    record.setUserProfileId(userId);
                    record.setName(req.name());
                    record.setDescription(req.description());
                    return record;
                }).toList();
    }

    public static List<WorkoutProgramRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutProgramRecord record = new WorkoutProgramRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    return record;
                }).toList();
    }

    public static List<WorkoutProgramRecord> fromDTO(UpdatePersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutProgramRecord record = new WorkoutProgramRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.description() != null) record.setDescription(req.description());
                    return record;
                }).toList();
    }
}
