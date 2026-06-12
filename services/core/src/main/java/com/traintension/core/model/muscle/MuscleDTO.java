package com.traintension.core.model.muscle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Kas DTO'lari")
public class MuscleDTO {
    @Schema(description = "Toplu global kas olusturma istegi")
    public record CreateGlobalsRequest(
            @Valid @Size(min = 1, max = 500) @NotNull
            @Schema(description = "Olusturulacak kas listesi (1-500 arasi)")
            List<CreateGlobalRequest> items
    ) {}

    @Schema(description = "Toplu global kas guncelleme istegi")
    public record UpdateGlobalsRequest(
            @Valid @Size(min = 1, max = 100) @NotNull
            @Schema(description = "Guncellenecek kas listesi")
            List<UpdateGlobalRequest> items
    ) {}

    @Schema(description = "Tek global kas olusturma istegi")
    public record CreateGlobalRequest(
            @NotBlank @Schema(description = "Kas adi", example = "Pectoralis Major") String name,
            @Size(max = 1000) @Schema(description = "Kas aciklamasi (opsiyonel)") String description,
            @Size(max = 2048) @Schema(description = "Medya URL'i (opsiyonel)") String mediaUrl
    ) {}

    @Schema(description = "Tek global kas guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdateGlobalRequest(
            @NotNull @Schema(description = "Guncellenecek kasin UUID'si") UUID id,
            @Schema(description = "Yeni kas adi (opsiyonel)") String name,
            @Size(max = 1000) @Schema(description = "Yeni aciklama (opsiyonel)") String description,
            @Size(max = 2048) @Schema(description = "Yeni medya URL'i (opsiyonel)") String mediaUrl
    ) {}

    @Builder
    @Schema(description = "Kas yanit modeli")
    public record Response(
            @Schema(description = "Kas UUID'si") UUID id,
            @Schema(description = "Kas adi") String name,
            @Schema(description = "Kas aciklamasi") String description,
            @Schema(description = "Medya URL'i") String mediaUrl,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Son guncellenme tarihi") OffsetDateTime updatedAt,
            @Schema(description = "Version numarasi") Long version
    ) {}
}
