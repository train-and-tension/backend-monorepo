package com.traintension.core.model.savedExercise;

import com.traintension.core.generated.tables.records.SavedExerciseRecord;
import com.traintension.core.model.savedExercise.SavedExerciseDTO.*;

import java.util.List;
import java.util.UUID;

public class SavedExerciseMapper {
    public static Response fromRecord(SavedExerciseRecord record) {
        return Response.builder()
                .id(record.getId())
                .exerciseId(record.getExerciseId())
                .version(record.getVersion())
                .build();
    }

    public static List<SavedExerciseRecord> fromDTO(CreatePersonalsRequest request, UUID userId) {
        return request.items().stream()
                .map(req -> {
                    SavedExerciseRecord record = new SavedExerciseRecord();
                    record.setUserProfileId(userId);
                    record.setExerciseId(req.exerciseId());
                    return record;
                }).toList();
    }
}
