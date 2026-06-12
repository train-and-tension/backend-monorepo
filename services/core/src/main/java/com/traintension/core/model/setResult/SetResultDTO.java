package com.traintension.core.model.setResult;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.enumValidation.ValidEnum;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.generated.enums.UnitSystem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Set sonucu DTO'lari")
public class SetResultDTO {

    @Schema(description = "Toplu set sonucu ekleme istegi")
    public record AddResultsRequest(
            @JsonProperty("workoutSessionId")
            @ValidUUIDv7 @NotNull
            @Schema(description = "Set sonuclarinin baglanacagi workout session UUID'si")
            UUID workoutSessionId,
            @JsonProperty("items")
            @Valid @Size(min = 1, max = 200) @NotNull
            @Schema(description = "Eklenecek set sonucu listesi")
            List<CreateRequest> items
    ) {}

    @Schema(description = "Toplu set sonucu guncelleme istegi")
    public record UpdateResultsRequest(
            @JsonProperty("items")
            @Valid @Size(min = 1, max = 200) @NotNull @UniqueIds
            @Schema(description = "Guncellenecek set sonucu listesi")
            List<UpdateRequest> items
    ) {}

    @Schema(description = "Tek set sonucu olusturma istegi")
    public record CreateRequest(
            @JsonProperty("exerciseId")
            @ValidUUIDv7 @NotNull @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @JsonProperty("orderNumber")
            @NotNull @Schema(description = "Sira numarasi") Integer orderNumber,
            @JsonProperty("duration")
            @Schema(description = "Sure - saniye") Integer duration,
            @JsonProperty("restDuration")
            @NotNull @Schema(description = "Dinlenme suresi - saniye") Integer restDuration,
            @JsonProperty("repCount")
            @Schema(description = "Tekrar sayisi") Integer repCount,
            @JsonProperty("weight")
            @NotNull @Schema(description = "Agirlik") BigDecimal weight,
            @JsonProperty("unit")
            @ValidEnum(enumClass = UnitSystem.class) @NotNull @Schema(description = "Birim") UnitSystem unit
    ) {}

    @Schema(description = "Tek set sonucu guncelleme istegi")
    public record UpdateRequest(
            @JsonProperty("id")
            @ValidUUIDv7 @NotNull @Schema(description = "Set sonucu UUID'si") UUID id,
            @JsonProperty("exerciseId")
            @ValidUUIDv7 @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @JsonProperty("orderNumber")
            @Schema(description = "Sira numarasi") Integer orderNumber,
            @JsonProperty("duration")
            @Schema(description = "Sure - saniye") Integer duration,
            @JsonProperty("restDuration")
            @Schema(description = "Dinlenme suresi - saniye") Integer restDuration,
            @JsonProperty("repCount")
            @Schema(description = "Tekrar sayisi") Integer repCount,
            @JsonProperty("weight")
            @Schema(description = "Agirlik") BigDecimal weight,
            @JsonProperty("unit")
            @ValidEnum(enumClass = UnitSystem.class) @Schema(description = "Birim") UnitSystem unit
    ) implements HasId {}

    @Builder
    @Schema(description = "Set sonucu yanit modeli")
    public record Response(
            UUID id,
            UUID workoutSessionId,
            UUID exerciseId,
            Integer orderNumber,
            Integer duration,
            Integer restDuration,
            Integer repCount,
            BigDecimal weight,
            UnitSystem unit,
            String exerciseNameSnapshot,
            Integer targetedRepCount,
            BigDecimal targetedWeight,
            Integer targetedDuration,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Long version
    ) {}
}
