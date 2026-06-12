package com.traintension.core.model.targetSet;


import com.traintension.core.common.util.UnitConverter;
import com.traintension.core.generated.tables.records.TargetSetRecord;
import com.traintension.core.model.targetSet.TargetSetDTO.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TargetSetMapper {
    public static List<TargetSetRecord> fromDTO(
            CreatePersonalsRequest request,
            UUID workoutDayId,
            UUID userId,
            int maxOrder
    ) {
        AtomicInteger order = new AtomicInteger(maxOrder + 1);

        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setUserProfileId(userId);
                    record.setWorkoutDayId(workoutDayId);
                    record.setExerciseId(req.exerciseId());
                    record.setWeightKg(UnitConverter.toStoredWeight(req.weight(), req.unit()));
                    record.setUnit(req.unit());
                    record.setRestDuration(req.restDuration());
                    record.setOrderNumber(order.getAndIncrement());
                    if (req.duration() != null && req.duration() > 0) record.setDuration(req.duration());
                    if (req.repCount() != null && req.repCount() > 0) record.setRepCount(req.repCount());
                    return record;
                }).toList();
    }

    public static List<TargetSetRecord> fromDTO(CreateGlobalsRequest request, UUID workoutDayId, int maxOrder) {
        AtomicInteger order = new AtomicInteger(maxOrder + 1);

        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setWorkoutDayId(workoutDayId);
                    record.setExerciseId(req.exerciseId());
                    record.setWeightKg(UnitConverter.toStoredWeight(req.weight(), req.unit()));
                    record.setUnit(req.unit());
                    record.setRestDuration(req.restDuration());
                    record.setOrderNumber(order.getAndIncrement());
                    if (req.duration() != null && req.duration() > 0) record.setDuration(req.duration());
                    if (req.repCount() != null && req.repCount() > 0) record.setRepCount(req.repCount());
                    return record;
                }).toList();
    }

    public static Response fromRecord(TargetSetRecord record) {
        return Response.builder()
                .id(record.getId())
                .exerciseId(record.getExerciseId())
                .workoutDayId(record.getWorkoutDayId())
                .weight(record.getWeightKg())
                .unit(record.getUnit())
                .restDuration(record.getRestDuration())
                .repCount(record.getRepCount())
                .duration(record.getDuration())
                .orderNumber(record.getOrderNumber())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .version(record.getVersion())
                .build();
    }

    public static ResponsesByWorkoutDay fromRecord(UUID workoutDayId, List<TargetSetRecord> records) {
        return ResponsesByWorkoutDay.builder()
                .workoutDayId(workoutDayId)
                .targetSets(
                        records.stream().map(TargetSetMapper::fromRecord).toList()
                ).build();
    }

    public static ResponsesByWorkoutProgram fromRecord(
            UUID workoutProgramId,
            Map<UUID, List<TargetSetRecord>> recordsByWorkoutDayId
    ) {
        List<ResponsesByWorkoutDay> dtosByWorkoutDay = recordsByWorkoutDayId.entrySet().stream()
                .map(entry -> ResponsesByWorkoutDay.builder()
                        .workoutDayId(entry.getKey())
                        .targetSets(entry.getValue().stream().map(TargetSetMapper::fromRecord).toList())
                        .build()
                ).toList();


        return ResponsesByWorkoutProgram.builder()
                .workoutProgramId(workoutProgramId)
                .targetSetByWorkoutDay(dtosByWorkoutDay)
                .build();
    }

    public static List<TargetSetRecord> fromDTO(UpdatePersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setId(req.id());
                    if (req.exerciseId() != null) record.setExerciseId(req.exerciseId());
                    if (req.weight() != null && req.unit() != null) {
                        record.setWeightKg(UnitConverter.toStoredWeight(req.weight(), req.unit()));
                        record.setUnit(req.unit());
                    }
                    if (req.restDuration() != null) record.setRestDuration(req.restDuration());

                    if (req.duration() != null && req.duration() > 0) {
                        record.setDuration(req.duration());
                        record.setRepCount(null);
                    } else if (req.repCount() != null && req.repCount() > 0) {
                        record.setRepCount(req.repCount());
                        record.setDuration(null);
                    }

                    return record;
                }).toList();
    }

    public static List<TargetSetRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setId(req.id());
                    if (req.exerciseId() != null) record.setExerciseId(req.exerciseId());
                    if (req.weight() != null && req.unit() != null) {
                        record.setWeightKg(UnitConverter.toStoredWeight(req.weight(), req.unit()));
                        record.setUnit(req.unit());
                    }
                    if (req.restDuration() != null) record.setRestDuration(req.restDuration());

                    if (req.duration() != null && req.duration() > 0) {
                        record.setDuration(req.duration());
                        record.setRepCount(null);
                    } else if (req.repCount() != null && req.repCount() > 0) {
                        record.setRepCount(req.repCount());
                        record.setDuration(null);
                    }

                    return record;
                }).toList();
    }

    public static List<TargetSetRecord> fromDTO(ReorderPersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setId(req.id());
                    if (req.orderNumber() != null) record.setOrderNumber(req.orderNumber());
                    return record;
                }).toList();
    }

    public static List<TargetSetRecord> fromDTO(ReorderGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    TargetSetRecord record = new TargetSetRecord();
                    record.setId(req.id());
                    if (req.orderNumber() != null) record.setOrderNumber(req.orderNumber());
                    return record;
                }).toList();
    }
}
