package com.traintension.core.model.workoutDay;

import com.traintension.core.generated.tables.records.WorkoutDayRecord;
import com.traintension.core.model.workoutDay.WorkoutDayDTO.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkoutDayMapper {

    //--------------------------RESPONSES------------------------------//
    public static Responses fromRecord(List<WorkoutDayRecord> records, UUID workoutProgramId) {
        List<Response> responseList = records.stream()
                .map(record -> Response.builder()
                        .id(record.getId())
                        .name(record.getName())
                        .orderNumber(record.getOrderNumber())
                        .isOff(record.getIsOff())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .toList();

        return Responses.builder()
                .workoutProgramId(workoutProgramId)
                .responses(responseList)
                .build();
    }

    //-------------------------RESPONSE-------------------------//
    public static Response fromRecord(WorkoutDayRecord record) {
        return Response.builder()
                .id(record.getId())
                .name(record.getName())
                .orderNumber(record.getOrderNumber())
                .isOff(record.getIsOff())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .version(record.getVersion())
                .build();
    }

    //--------------------------------CREATE-------------------------------//
    public static List<WorkoutDayRecord> fromDTO(
            CreateGlobalsRequest request,
            UUID workoutProgramId,
            Integer maxOrder
    ) {

        AtomicInteger orderCounter = new AtomicInteger(maxOrder + 1);
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setWorkoutProgramId(workoutProgramId);
                    record.setName(req.name());
                    record.setIsOff(req.isOff());
                    record.setOrderNumber(orderCounter.getAndIncrement());
                    return record;
                }).toList();
    }

    public static List<WorkoutDayRecord> fromDTO(
            CreatePersonalsRequest request,
            UUID workoutProgramId,
            Integer maxOrder,
            UUID userId
    ) {
        AtomicInteger orderCounter = new AtomicInteger(maxOrder + 1);
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setUserProfileId(userId);
                    record.setWorkoutProgramId(workoutProgramId);
                    record.setName(req.name());
                    record.setIsOff(req.isOff());
                    record.setOrderNumber(orderCounter.getAndIncrement());
                    return record;
                }).toList();
    }

    //---------------------------------------UPDATE----------------------------------------//
    public static List<WorkoutDayRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.isOff() != null) record.setIsOff(req.isOff());
                    return record;
                }).toList();
    }

    public static List<WorkoutDayRecord> fromDTO(UpdatePersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setId(req.id());
                    if (req.name() != null) record.setName(req.name());
                    if (req.isOff() != null) record.setIsOff(req.isOff());
                    return record;
                }).toList();
    }

    public static List<WorkoutDayRecord> fromDTO(ReorderPersonalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setId(req.id());
                    if (req.orderNumber() != null) record.setOrderNumber(req.orderNumber());
                    return record;
                }).toList();
    }

     public static List<WorkoutDayRecord> fromDTO(ReorderGlobalsRequest request) {
        return request.items().stream()
                .map(req -> {
                    WorkoutDayRecord record = new WorkoutDayRecord();
                    record.setId(req.id());
                    if (req.orderNumber() != null) record.setOrderNumber(req.orderNumber());
                    return record;
                }).toList();
    }
}
