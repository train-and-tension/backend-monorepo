package com.traintension.core.model.workoutDay;

import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.model.workoutDay.annotations.OrderedRequest;
import com.traintension.core.model.workoutDay.annotations.UniqueOrderNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Antrenman gunu DTO'lari")
public class WorkoutDayDTO {
    //------------------------MULTIPLE ORDER CHANGE--------------------------//
    @Schema(description = "Toplu kisisel antrenman gunu siralama istegi")
    public record ReorderPersonalsRequest(
            @Size(min = 1, max = 30) @Valid @NotNull @UniqueOrderNumbers @UniqueIds
            @Schema(description = "Siralanacak gun listesi (ID'ler ve orderNumber'lar benzersiz olmali)")
            List<ReorderPersonalRequest> items
    ) {}

    @Schema(description = "Toplu global antrenman gunu siralama istegi")
    public record ReorderGlobalsRequest(
            @Size(min = 1, max = 30) @Valid @NotNull @UniqueOrderNumbers @UniqueIds
            @Schema(description = "Siralanacak gun listesi")
            List<ReorderGlobalRequest> items
    ) {}

    //--------------------------MULTIPLE CREATE------------------------------//
    @Schema(description = "Toplu global antrenman gunu olusturma istegi")
    public record CreateGlobalsRequest(
            @Size(min = 1, max = 500) @Valid @NotNull
            @Schema(description = "Olusturulacak gun listesi (1-500 arasi)")
            List<CreateGlobalRequest> items
    ) {}

    @Schema(description = "Toplu kisisel antrenman gunu olusturma istegi")
    public record CreatePersonalsRequest(
            @Size(min = 1, max = 100) @Valid @NotNull
            @Schema(description = "Olusturulacak gun listesi (1-100 arasi)")
            List<CreatePersonalRequest> items
    ) {}

    //--------------------MULTIPLE UPDATE------------------------//
    @Schema(description = "Toplu global antrenman gunu guncelleme istegi")
    public record UpdateGlobalsRequest(
            @NotNull @Size(min = 1, max = 200) @UniqueIds @Valid
            @Schema(description = "Guncellenecek gun listesi")
            List<UpdateGlobalRequest> items
    ) {}

    @Schema(description = "Toplu kisisel antrenman gunu guncelleme istegi")
    public record UpdatePersonalsRequest(
            @NotNull @Size(min = 1, max = 200) @UniqueIds @Valid
            @Schema(description = "Guncellenecek gun listesi")
            List<UpdatePersonalRequest> items
    ) {}

    //--------------------SINGLE CREATE------------------------//
    @Schema(description = "Tek global antrenman gunu olusturma istegi")
    public record CreateGlobalRequest(
            @NotBlank @Schema(description = "Gun adi", example = "Push Day") String name,
            @NotNull @Schema(description = "Dinlenme gunu mu?", example = "false") Boolean isOff
    ) {}

    @Schema(description = "Tek kisisel antrenman gunu olusturma istegi")
    public record CreatePersonalRequest(
            @NotBlank @Schema(description = "Gun adi", example = "Leg Day") String name,
            @NotNull @Schema(description = "Dinlenme gunu mu?", example = "false") Boolean isOff
    ) {}

    //--------------------SINGLE UPDATE------------------------//
    @Schema(description = "Tek global antrenman gunu guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdateGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek gunun UUID'si") UUID id,
            @Schema(description = "Yeni gun adi (opsiyonel)") String name,
            @Schema(description = "Dinlenme gunu mu? (opsiyonel)") Boolean isOff
    ) implements HasId {}

    @Schema(description = "Tek kisisel antrenman gunu guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdatePersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Guncellenecek gunun UUID'si") UUID id,
            @Schema(description = "Yeni gun adi (opsiyonel)") String name,
            @Schema(description = "Dinlenme gunu mu? (opsiyonel)") Boolean isOff
    ) implements HasId {}

    //-----------------UPDATE ORDERS REQUESTS-------------------//
    @Schema(description = "Tek global antrenman gunu siralama istegi")
    public record ReorderGlobalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Gunun UUID'si") UUID id,
            @Min(1) @Max(30) @Schema(description = "Yeni sira numarasi (1-30 arasi)", example = "1") Integer orderNumber
    ) implements OrderedRequest, HasId {}

    @Schema(description = "Tek kisisel antrenman gunu siralama istegi")
    public record ReorderPersonalRequest(
            @ValidUUIDv7 @NotNull @Schema(description = "Gunun UUID'si") UUID id,
            @Min(1) @Max(30) @Schema(description = "Yeni sira numarasi (1-30 arasi)", example = "2") Integer orderNumber
    ) implements OrderedRequest, HasId {}

    //--------------------RESPONSE------------------------//
    @Builder
    @Schema(description = "Antrenman gunu yanit modeli")
    public record Response(
            @Schema(description = "Gun UUID'si") UUID id,
            @Schema(description = "Gun adi") String name,
            @Schema(description = "Sira numarasi") Integer orderNumber,
            @Schema(description = "Dinlenme gunu mu?") Boolean isOff,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Son guncellenme tarihi") OffsetDateTime updatedAt,
            @Schema(description = "Version numarasi") Long version
    ) {}

    @Builder
    @Schema(description = "Antrenman gunleri toplu yanit modeli - Workout program baglami ile birlikte")
    public record Responses(
            @Schema(description = "Antrenman programi UUID'si") UUID workoutProgramId,
            @Schema(description = "Antrenman gunu listesi (sirali)") List<Response> responses
    ) {}

}
