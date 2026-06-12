package com.traintension.core.model.savedExercise;

import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Schema(description = "Kayitli egzersiz DTO'lari")
public class SavedExerciseDTO {
    @Schema(description = "Toplu egzersiz kaydetme istegi")
    public record CreatePersonalsRequest(
            @Valid @Size(min = 1, max = 200) @NotNull
            @Schema(description = "Kaydedilecek egzersiz listesi (1-200 arasi)")
            List<CreatePersonalRequest> items
    ) {}

    @Schema(description = "Tek egzersiz kaydetme istegi")
    public record CreatePersonalRequest(
            @ValidUUIDv7 @NotNull
            @Schema(description = "Kaydedilecek egzersizin UUID'si")
            UUID exerciseId
    ) {}

    @Builder
    @Schema(description = "Kayitli egzersiz yanit modeli")
    public record Response(
            @Schema(description = "Kayitli egzersiz kaydi UUID'si") UUID id,
            @Schema(description = "Kaydedilen egzersiz UUID'si") UUID exerciseId,
            @Schema(description = "Version numarasi") Long version
    ) {}
}
