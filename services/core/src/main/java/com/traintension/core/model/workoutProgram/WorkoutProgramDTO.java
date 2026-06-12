package com.traintension.core.model.workoutProgram;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.model.targetSet.TargetSetDTO;
import com.traintension.core.model.workoutDay.WorkoutDayDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Antrenman programi DTO'lari")
public class WorkoutProgramDTO {

    //-------------------CREATE LIST-----------------//
    @Schema(description = "Toplu global antrenman programi olusturma istegi")
    public record CreateGlobalsRequest(
            @Valid @Size(min = 1, max = 300) @NotNull
            @Schema(description = "Olusturulacak program listesi (1-300 arasi)")
            List<CreateGlobalRequest> items
    ) {}

    @Schema(description = "Toplu kisisel antrenman programi olusturma istegi")
    public record CreatePersonalsRequest(
            @Valid @Size(min = 1, max = 20) @NotNull
            @Schema(description = "Olusturulacak program listesi (1-20 arasi)")
            List<CreatePersonalRequest> items
    ) {}

    //--------------------UPDATE LIST--------------------//
    @Schema(description = "Toplu global antrenman programi guncelleme istegi")
    public record UpdateGlobalsRequest(
            @Valid @Size(min = 1, max = 200) @NotNull @UniqueIds
            @Schema(description = "Guncellenecek program listesi")
            List<UpdateGlobalRequest> items
    ) {}

    @Schema(description = "Toplu kisisel antrenman programi guncelleme istegi")
    public record UpdatePersonalsRequest(
            @Valid @Size(min = 1, max = 10) @NotNull @UniqueIds
            @Schema(description = "Guncellenecek program listesi")
            List<UpdatePersonalRequest> items
    ) {}

    //--------------------CREATE------------------------//
    @Schema(description = "Tek kisisel antrenman programi olusturma istegi")
    public record CreatePersonalRequest(
            @NotBlank @Schema(description = "Program adi", example = "Push/Pull/Legs") String name,
            @Schema(description = "Program aciklamasi (opsiyonel)", example = "Haftada 6 gun PPL programi") String description
    ) {}

    @Schema(description = "Tek global antrenman programi olusturma istegi")
    public record CreateGlobalRequest(
            @NotBlank @Schema(description = "Program adi", example = "Full Body") String name,
            @Schema(description = "Program aciklamasi (opsiyonel)") String description
    ) {}

    //-----------------------------UPDATE-------------------------//
    @Schema(description = "Tek kisisel antrenman programi guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdatePersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek programin UUID'si") UUID id,
            @Schema(description = "Yeni program adi (opsiyonel)") String name,
            @Schema(description = "Yeni aciklama (opsiyonel)") String description
    ) implements HasId {}

    @Schema(description = "Tek global antrenman programi guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdateGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek programin UUID'si") UUID id,
            @Schema(description = "Yeni program adi (opsiyonel)") String name,
            @Schema(description = "Yeni aciklama (opsiyonel)") String description
    ) implements HasId {}

    //-----------------------------ACTIONS-------------------------//
    @Schema(description = "Antrenman programi aktiflestirme istegi")
    public record ActivateRequest(
            @NotNull @Future
            @Schema(description = "Programin baslayacagi tarih. Mutlaka gelecek bir tarih olmalidir.", example = "2026-05-09")
            @JsonAlias("start_date")
            LocalDate startDate
    ) {}

    @Builder
    @Schema(description = "Sistem programindan uretilen bagimsiz kisisel program grafigi")
    public record DuplicateResponse(
            @Schema(description = "Yeni program UUID'si") UUID id,
            @Schema(description = "Program sahibi kullanici UUID'si") UUID userProfileId,
            @Schema(description = "Program adi") String name,
            @Schema(description = "Program aciklamasi") String description,
            @Schema(description = "Program aktif mi?") Boolean isActive,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Son guncellenme tarihi") OffsetDateTime updatedAt,
            @Schema(description = "Yeni olusturulan kisisel program") Response workoutProgram,
            @Schema(description = "Yeni programa kopyalanan antrenman gunleri") WorkoutDayDTO.Responses workoutDays,
            @Schema(description = "Yeni workout day'lere kopyalanan hedef setler") TargetSetDTO.ResponsesByWorkoutProgram targetSets
    ) {}

    //------------------------RESPONSE-----------------------//
    @Builder
    @Schema(description = "Antrenman programi yanit modeli")
    public record Response(
            @Schema(description = "Program UUID'si") UUID id,
            @Schema(description = "Program sahibi kullanici UUID'si. Sistem programlarinda null olur.") UUID userProfileId,
            @Schema(description = "Program adi") String name,
            @Schema(description = "Program aciklamasi") String description,
            @Schema(description = "Program aktif mi?") Boolean isActive,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Son guncellenme tarihi") OffsetDateTime updatedAt
    ) {}
}
