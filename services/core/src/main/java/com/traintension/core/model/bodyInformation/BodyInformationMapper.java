package com.traintension.core.model.bodyInformation;

import com.traintension.core.common.util.UnitConverter;
import com.traintension.core.generated.tables.records.BodyInformationRecord;
import com.traintension.core.generated.tables.records.MeasurementHistoryRecord;
import com.traintension.core.model.bodyInformation.BodyInformationDTO.*;

import java.util.UUID;

public class BodyInformationMapper {
    public static Response fromRecord(BodyInformationRecord record) {
        return Response.builder()
                .id(record.getId())
                .height(UnitConverter.toResponseHeight(record.getHeightCm(), record.getUnit()))
                .weight(UnitConverter.toResponseWeight(record.getWeightKg(), record.getUnit()))
                .unit(record.getUnit())
                .birthDate(record.getBirthDate())
                .gender(record.getGender())
                .activityLevel(record.getActivityLevel())
                .weightGoal(record.getWeightGoal())
                .trainingGoal(record.getTrainingGoal())
                .createdAt(record.getCreatedAt())
                .version(record.getVersion())
                .build();
    }

    public static BodyInformationRecord fromDTO(CreatePersonalRequest request, UUID userId) {
        BodyInformationRecord record = new BodyInformationRecord();
        record.setUserProfileId(userId);
        record.setHeightCm(UnitConverter.toStoredHeight(request.height(), request.unit()));
        record.setWeightKg(UnitConverter.toStoredWeight(request.weight(), request.unit()));
        record.setUnit(request.unit());
        record.setBirthDate(request.birthDate());
        record.setGender(request.gender());
        record.setActivityLevel(request.activityLevel());
        record.setWeightGoal(request.weightGoal());
        record.setTrainingGoal(request.trainingGoal());
        return record;
    }

    public static BodyInformationRecord fromDTO(UpdateProfileRequest request) {
        BodyInformationRecord record = new BodyInformationRecord();
        if (request.birthDate() != null) record.setBirthDate(request.birthDate());
        if (request.gender() != null) record.setGender(request.gender());
        if (request.activityLevel() != null) record.setActivityLevel(request.activityLevel());
        if (request.weightGoal() != null) record.setWeightGoal(request.weightGoal());
        if (request.trainingGoal() != null) record.setTrainingGoal(request.trainingGoal());
        return record;
    }

    public static BodyInformationRecord fromDTO(UpdateMeasurementsRequest request) {
        BodyInformationRecord record = new BodyInformationRecord();
        record.setHeightCm(UnitConverter.toStoredHeight(request.height(), request.unit()));
        record.setWeightKg(UnitConverter.toStoredWeight(request.weight(), request.unit()));
        record.setUnit(request.unit());
        return record;
    }

    public static MeasurementHistoryRecord toMeasurementHistory(CreatePersonalRequest request, UUID userId) {
        MeasurementHistoryRecord record = new MeasurementHistoryRecord();
        record.setUserProfileId(userId);
        record.setHeightCm(UnitConverter.toStoredHeight(request.height(), request.unit()));
        record.setWeightKg(UnitConverter.toStoredWeight(request.weight(), request.unit()));
        record.setUnit(request.unit());
        return record;
    }

    public static MeasurementHistoryRecord toMeasurementHistory(UpdateMeasurementsRequest request, UUID userId) {
        MeasurementHistoryRecord record = new MeasurementHistoryRecord();
        record.setUserProfileId(userId);
        record.setHeightCm(UnitConverter.toStoredHeight(request.height(), request.unit()));
        record.setWeightKg(UnitConverter.toStoredWeight(request.weight(), request.unit()));
        record.setUnit(request.unit());
        return record;
    }
}
