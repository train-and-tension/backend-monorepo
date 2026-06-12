package com.traintension.core.model.exerciseMuscle;

import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.enumValidation.ValidEnum;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.generated.enums.ActivationLevel;
import com.traintension.core.model.exercise.ExerciseDTO;
import com.traintension.core.model.muscle.MuscleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.*;

@Schema(description = "Egzersiz-Kas iliskisi DTO'lari")
public class ExerciseMuscleDTO {

    @Schema(description = "Toplu kisisel egzersiz-kas iliskisi olusturma istegi")
    public record CreatePersonalsRequest(
            @Valid @NotNull @Size(min = 1, max = 100)
            @Schema(description = "Olusturulacak iliski listesi (1-100 arasi)")
            List<CreatePersonalRequest> items
    ) {}

    @Schema(description = "Toplu global egzersiz-kas iliskisi olusturma istegi")
    public record CreateGlobalsRequest(
            @Valid @NotNull @Size(min = 1, max = 1000)
            @Schema(description = "Olusturulacak iliski listesi (1-1000 arasi)")
            List<CreateGlobalRequest> items
    ) {}

    @Schema(description = "Tek kisisel egzersiz-kas iliskisi olusturma istegi")
    public record CreatePersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @ValidUUIDv7 @NotNull @Schema(description = "Kas UUID'si") UUID muscleId,
            @ValidEnum(enumClass = ActivationLevel.class) @NotNull
            @Schema(description = "Aktivasyon seviyesi", example = "PRIMARY") ActivationLevel activationLevel
    ) {}

    @Schema(description = "Tek global egzersiz-kas iliskisi olusturma istegi")
    public record CreateGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @ValidUUIDv7 @NotNull @Schema(description = "Kas UUID'si") UUID muscleId,
            @ValidEnum(enumClass = ActivationLevel.class) @NotNull
            @Schema(description = "Aktivasyon seviyesi", example = "SECONDARY") ActivationLevel activationLevel
    ) {}

    @Builder
    @Schema(description = "Egzersiz-kas iliskisi yanit modeli")
    public record Response(
            @Schema(description = "Iliski UUID'si") UUID id,
            @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @Schema(description = "Kas UUID'si") UUID muscleId,
            @Schema(description = "Aktivasyon seviyesi") ActivationLevel activationLevel,
            @Schema(description = "Version numarasi") Long version
    ) {}

    @Schema(description = "Egzersizin calistirdigi kaslar, aktivasyon seviyesine gore gruplandirilmis")
    public record MuscleResponse(
            @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @Schema(description = "Aktivasyon seviyesine gore gruplandirilmis kas listesi")
            Map<ActivationLevel, List<MuscleDTO.Response>> muscles
    ) {}

    @Schema(description = "Kasi calistiran egzersizler, aktivasyon seviyesine gore gruplandirilmis")
    public record ExerciseResponse(
            @Schema(description = "Kas UUID'si") UUID muscleId,
            @Schema(description = "Aktivasyon seviyesine gore gruplandirilmis egzersiz listesi")
            Map<ActivationLevel, List<ExerciseDTO.Response>> exercises
    ) {}
}
