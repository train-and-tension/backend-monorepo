package com.traintension.core.model.targetSet;

import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.enumValidation.ValidEnum;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.generated.enums.UnitSystem;
import com.traintension.core.model.targetSet.annotation.ValidRepOrDuration;
import com.traintension.core.model.workoutDay.annotations.OrderedRequest;
import com.traintension.core.model.workoutDay.annotations.UniqueOrderNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Hedef set DTO'lari - Bir egzersizin antrenman gunundeki set detaylarini tanimlar")
public class TargetSetDTO {
    //------------------------MULTIPLE CREATE----------------------------//
    @Schema(description = "Toplu kisisel hedef set olusturma istegi")
    public record CreatePersonalsRequest(
            @Valid @Size(min = 1, max = 100) @NotNull
            @Schema(description = "Olusturulacak hedef set listesi (1-100 arasi)")
            List<CreatePersonalRequest> items
    ) {}

    @Schema(description = "Toplu global hedef set olusturma istegi")
    public record CreateGlobalsRequest(
            @Valid @Size(min = 1, max = 500) @NotNull
            @Schema(description = "Olusturulacak hedef set listesi (1-500 arasi)")
            List<CreateGlobalRequest> items
    ) {}

    //-----------------------MULTIPLE UPDATE & REORDER---------------------------//
    @Schema(description = "Toplu kisisel hedef set siralama istegi")
    public record ReorderPersonalsRequest(
            @Valid @Size(min = 1, max = 100) @NotNull @UniqueIds @UniqueOrderNumbers
            @Schema(description = "Siralanacak hedef set listesi (ID'ler ve orderNumber'lar benzersiz olmali)")
            List<ReorderPersonalRequest> items
    ) {}

    @Schema(description = "Toplu global hedef set siralama istegi")
    public record ReorderGlobalsRequest(
            @Valid @Size(min = 1, max = 500) @NotNull @UniqueIds @UniqueOrderNumbers
            @Schema(description = "Siralanacak hedef set listesi")
            List<ReorderGlobalRequest> items
    ) {}

    @Schema(description = "Toplu kisisel hedef set guncelleme istegi")
    public record UpdatePersonalsRequest(
            @Valid @Size(min = 1, max = 100) @NotNull @UniqueIds
            @Schema(description = "Guncellenecek hedef set listesi")
            List<UpdatePersonalRequest> items
    ) {}

    @Schema(description = "Toplu global hedef set guncelleme istegi")
    public record UpdateGlobalsRequest(
            @Valid @Size(min = 1, max = 500) @NotNull @UniqueIds
            @Schema(description = "Guncellenecek hedef set listesi")
            List<UpdateGlobalRequest> items
    ) {}

    //-----------------------SINGLE CREATE---------------------------//
    @ValidRepOrDuration(mode = ValidRepOrDuration.Mode.CREATE)
    @Schema(description = "Tek kisisel hedef set olusturma istegi. repCount ve duration'dan en az biri dolu olmalidir.")
    public record CreatePersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @NotNull @Schema(description = "Tekrar sayisi (0 ise sure bazli set)", example = "12") Integer repCount,
            @NotNull @Schema(description = "Agirlik degeri", example = "60.0") BigDecimal weight,
            @ValidEnum(enumClass = UnitSystem.class) @Schema(description = "Olcu birimi", example = "METRIC") UnitSystem unit,
            @NotNull @Schema(description = "Dinlenme suresi (saniye)", example = "90") Integer restDuration,
            @NotNull @Schema(description = "Set suresi (saniye, 0 ise tekrar bazli)", example = "0") Integer duration
    ) implements RepOrDuration {}

    @ValidRepOrDuration(mode = ValidRepOrDuration.Mode.CREATE)
    @Schema(description = "Tek global hedef set olusturma istegi. repCount ve duration'dan en az biri dolu olmalidir.")
    public record CreateGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @NotNull @Schema(description = "Tekrar sayisi", example = "10") Integer repCount,
            @NotNull @Schema(description = "Agirlik degeri", example = "80.0") BigDecimal weight,
            @ValidEnum(enumClass = UnitSystem.class) @Schema(description = "Olcu birimi", example = "METRIC") UnitSystem unit,
            @NotNull @Schema(description = "Dinlenme suresi (saniye)", example = "120") Integer restDuration,
            @NotNull @Schema(description = "Set suresi (saniye)", example = "0") Integer duration
    ) implements RepOrDuration {}

    //-----------------------SINGLE REORDER---------------------------//
    @Schema(description = "Tek kisisel hedef set siralama istegi")
    public record ReorderPersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Hedef set UUID'si") UUID id,
            @Min(1) @Max(500) @Schema(description = "Yeni sira numarasi (1-500 arasi)", example = "1") Integer orderNumber
    ) implements OrderedRequest, HasId {}

    @Schema(description = "Tek global hedef set siralama istegi")
    public record ReorderGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Hedef set UUID'si") UUID id,
            @Min(1) @Max(500) @Schema(description = "Yeni sira numarasi (1-500 arasi)", example = "2") Integer orderNumber
    ) implements OrderedRequest, HasId {}

    //-----------------------SINGLE UPDATE---------------------------//
    @Schema(description = "Tek kisisel hedef set guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdatePersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek hedef set UUID'si") UUID id,
            @Schema(description = "Yeni tekrar sayisi (opsiyonel)") Integer repCount,
            @Schema(description = "Yeni agirlik degeri (opsiyonel)") BigDecimal weight,
            @Schema(description = "Yeni olcu birimi (opsiyonel)") UnitSystem unit,
            @Schema(description = "Yeni dinlenme suresi - saniye (opsiyonel)") Integer restDuration,
            @Schema(description = "Yeni set suresi - saniye (opsiyonel)") Integer duration,
            @ValidUUIDv7 @Schema(description = "Yeni egzersiz UUID'si (opsiyonel)") UUID exerciseId
    ) implements HasId {}

    @Schema(description = "Tek global hedef set guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdateGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek hedef set UUID'si") UUID id,
            @Schema(description = "Yeni tekrar sayisi (opsiyonel)") Integer repCount,
            @Schema(description = "Yeni agirlik degeri (opsiyonel)") BigDecimal weight,
            @Schema(description = "Yeni olcu birimi (opsiyonel)") UnitSystem unit,
            @Schema(description = "Yeni dinlenme suresi - saniye (opsiyonel)") Integer restDuration,
            @Schema(description = "Yeni set suresi - saniye (opsiyonel)") Integer duration,
            @ValidUUIDv7 @Schema(description = "Yeni egzersiz UUID'si (opsiyonel)") UUID exerciseId
    ) implements HasId {}

    @Builder
    @Schema(description = "Workout program bazinda hedef set yaniti - Tum workout day'lerin hedef setlerini icerir")
    public record ResponsesByWorkoutProgram(
            @Schema(description = "Antrenman programi UUID'si") UUID workoutProgramId,
            @Schema(description = "Her workout day icin hedef set listesi") List<ResponsesByWorkoutDay> targetSetByWorkoutDay
    ) {}

    @Builder
    @Schema(description = "Workout day bazinda hedef set yaniti")
    public record ResponsesByWorkoutDay(
            @Schema(description = "Antrenman gunu UUID'si") UUID workoutDayId,
            @Schema(description = "Bu gune ait hedef setler (sirali)") List<Response> targetSets
    ) {}

    @Builder
    @Schema(description = "Hedef set yanit modeli")
    public record Response(
            @Schema(description = "Hedef set UUID'si") UUID id,
            @Schema(description = "Egzersiz UUID'si") UUID exerciseId,
            @Schema(description = "Antrenman gunu UUID'si") UUID workoutDayId,
            @Schema(description = "Agirlik degeri") BigDecimal weight,
            @Schema(description = "Olcu birimi") UnitSystem unit,
            @Schema(description = "Dinlenme suresi (saniye)") Integer restDuration,
            @Schema(description = "Tekrar sayisi") Integer repCount,
            @Schema(description = "Set suresi (saniye)") Integer duration,
            @Min(1) @Max(500) @Schema(description = "Sira numarasi") Integer orderNumber,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Son guncellenme tarihi") OffsetDateTime updatedAt,
            @Schema(description = "Version numarasi") Long version
    ) {}

    public interface RepOrDuration {
        Integer repCount();
        Integer duration();
    }
}
