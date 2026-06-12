package com.traintension.core.model.exerciseMuscle;

import com.traintension.common.exception.custom.InternalServerErrorException;
import com.traintension.common.exception.custom.SecureException;
import com.traintension.core.generated.Tables;
import com.traintension.core.generated.enums.ActivationLevel;
import com.traintension.core.generated.tables.records.ExerciseMuscleRecord;
import com.traintension.core.generated.tables.records.ExerciseRecord;
import com.traintension.core.generated.tables.records.MuscleRecord;
import com.traintension.core.model.exerciseMuscle.ExerciseMuscleDTO.*;
import com.traintension.core.model.muscle.MuscleDTO;
import com.traintension.core.model.muscle.MuscleMapper;
import org.jooq.Record;
import org.jooq.Result;

import java.util.*;

import static com.traintension.core.generated.Tables.*;


public class ExerciseMuscleMapper {
    public static Response fromRecord(ExerciseMuscleRecord record) {
        return Response.builder()
                .id(record.getId())
                .exerciseId(record.getExerciseId())
                .muscleId(record.getMuscleId())
                .activationLevel(record.getActivationLevel())
                .version(record.getVersion())
                .build();
    }

    public static List<ExerciseMuscleRecord> fromDTO(CreatePersonalsRequest request, UUID userId) {
        return request.items().stream()
                .map(i -> {
                    ExerciseMuscleRecord record = new ExerciseMuscleRecord();
                    record.setUserProfileId(userId);
                    record.setExerciseId(i.exerciseId());
                    record.setMuscleId(i.muscleId());
                    record.setActivationLevel(i.activationLevel());
                    return record;
                }).toList();
    }

    public static List<ExerciseMuscleRecord> fromDTO(CreateGlobalsRequest request) {
        return request.items().stream()
                .map(i -> {
                    ExerciseMuscleRecord record = new ExerciseMuscleRecord();
                    record.setExerciseId(i.exerciseId());
                    record.setMuscleId(i.muscleId());
                    record.setActivationLevel(i.activationLevel());
                    return record;
                }).toList();
    }

}
