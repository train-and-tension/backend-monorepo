package com.traintension.core.model.bodyInformation;

import com.traintension.core.common.annotations.enumValidation.ValidEnum;
import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.generated.enums.*;
import com.traintension.core.model.bodyInformation.annotations.AgeRange;
import com.traintension.core.model.bodyInformation.annotations.ValidBodyMeasurements;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Vücut bilgisi DTO'ları")
public class BodyInformationDTO {

    @ValidBodyMeasurements
    @Schema(description = "Vücut bilgisi oluşturma isteği. Tüm alanlar zorunludur.")
    public record CreatePersonalRequest(
            @NotNull
            @Schema(description = "Boy (birime göre cm veya inch)", example = "175.5")
            BigDecimal height,

            @NotNull
            @Schema(description = "Kilo (birime göre kg veya lbs)", example = "72.3")
            BigDecimal weight,

            @NotNull
            @ValidEnum(enumClass = UnitSystem.class)
            @Schema(description = "Ölçü birimi sistemi", example = "METRIC")
            UnitSystem unit,

            @NotNull
            @Past(message = "Birth date must be in the past")
            @AgeRange
            @Schema(description = "Doğum tarihi (geçmiş bir tarih olmalı)", example = "1995-06-15")
            LocalDate birthDate,

            @NotNull
            @ValidEnum(enumClass = Gender.class)
            @Schema(description = "Cinsiyet", example = "MALE")
            Gender gender,

            @NotNull
            @ValidEnum(enumClass = ActivityLevel.class)
            @Schema(description = "Aktivite seviyesi", example = "MODERATE")
            ActivityLevel activityLevel,

            @NotNull
            @ValidEnum(enumClass = TrainingGoal.class)
            @Schema(description = "Antrenman hedefi", example = "MUSCLE_GAIN")
            TrainingGoal trainingGoal,

            @NotNull
            @ValidEnum(enumClass = WeightGoal.class)
            @Schema(description = "Kilo hedefi", example = "MAINTAIN")
            WeightGoal weightGoal
    ) {
    }

    @Builder
    @Schema(description = "Vücut bilgisi yanıt modeli")
    public record Response(
            @Schema(description = "Kayıt UUID'si")
            UUID id,
            @Schema(description = "Boy değeri", example = "175.5")
            BigDecimal height,
            @Schema(description = "Kilo değeri", example = "72.3")
            BigDecimal weight,
            @Schema(description = "Ölçü birimi sistemi")
            UnitSystem unit,
            @Schema(description = "Doğum tarihi")
            LocalDate birthDate,
            @Schema(description = "Cinsiyet")
            Gender gender,
            @Schema(description = "Aktivite seviyesi")
            ActivityLevel activityLevel,
            @Schema(description = "Oluşturulma tarihi")
            OffsetDateTime createdAt,
            @Schema(description = "Son güncellenme tarihi")
            OffsetDateTime updatedAt,
            @Schema(description = "Antrenman hedefi")
            TrainingGoal trainingGoal,
            @Schema(description = "Kilo hedefi")
            WeightGoal weightGoal,
            @Schema(description = "Optimistic locking version numarası")
            Long version
    ) {
    }

    @ValidBodyMeasurements
    @Schema(description = "Ölçüm güncelleme isteği. Boy, kilo ve birim sistemi zorunludur.")
    public record UpdateMeasurementsRequest(
            @ValidUUIDv7
            @NotNull
            @Schema(description = "Güncellenecek kaydın UUID'si")
            UUID id,
            @NotNull
            @Schema(description = "Yeni boy değeri", example = "176.0")
            BigDecimal height,
            @NotNull
            @Schema(description = "Yeni kilo değeri", example = "73.0")
            BigDecimal weight,
            @NotNull
            @ValidEnum(enumClass = UnitSystem.class)
            @NotNull
            @Schema(description = "Ölçü birimi sistemi", example = "METRIC")
            UnitSystem unit
    ) {
    }

    @Schema(description = "Profil bilgisi güncelleme isteği. Sadece gönderilen alanlar güncellenir (partial update).")
    public record UpdateProfileRequest(
            @ValidUUIDv7
            @NotNull
            @Schema(description = "Güncellenecek kaydın UUID'si")
            UUID id,

            @Past(message = "Birth date must be in the past")
            @AgeRange
            @Schema(description = "Yeni doğum tarihi (opsiyonel)", example = "1995-06-15")
            LocalDate birthDate,

            @ValidEnum(enumClass = Gender.class)
            @Schema(description = "Yeni cinsiyet (opsiyonel)", example = "MALE")
            Gender gender,

            @ValidEnum(enumClass = ActivityLevel.class)
            @Schema(description = "Yeni aktivite seviyesi (opsiyonel)", example = "MODERATE")
            ActivityLevel activityLevel,

            @ValidEnum(enumClass = TrainingGoal.class)
            @Schema(description = "Yeni antrenman hedefi (opsiyonel)", example = "MUSCLE_GAIN")
            TrainingGoal trainingGoal,

            @ValidEnum(enumClass = WeightGoal.class)
            @Schema(description = "Yeni kilo hedefi (opsiyonel)", example = "MAINTAIN")
            WeightGoal weightGoal
    ) {
    }
}
