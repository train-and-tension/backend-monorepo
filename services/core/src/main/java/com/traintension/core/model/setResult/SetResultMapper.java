package com.traintension.core.model.setResult;

import com.traintension.core.generated.tables.records.SetResultRecord;
import com.traintension.core.model.setResult.SetResultDTO.Response;

public class SetResultMapper {
    public static Response fromRecord(SetResultRecord record) {
        return Response.builder()
                .id(record.getId())
                .workoutSessionId(record.getWorkoutSessionId())
                .exerciseId(record.getExerciseId())
                .orderNumber(record.getOrderNumber())
                .duration(record.getDuration())
                .restDuration(record.getRestDuration())
                .repCount(record.getRepCount())
                .weight(record.getWeightKg())
                .unit(record.getUnit())
                .exerciseNameSnapshot(record.getExerciseNameSnapshot())
                .targetedRepCount(record.getTargetedRepCount())
                .targetedWeight(record.getTargetedWeight())
                .targetedDuration(record.getTargetedDuration())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .version(record.getVersion())
                .build();
    }
}
