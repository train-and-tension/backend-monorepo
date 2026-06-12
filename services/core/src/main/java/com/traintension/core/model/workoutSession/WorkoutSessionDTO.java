package com.traintension.core.model.workoutSession;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traintension.core.model.setResult.SetResultDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Workout session DTO'lari")
public class WorkoutSessionDTO {

    @Schema(description = "Quick workout kaydetme istegi")
    public record SaveQuickExerciseRequest(
            @JsonProperty("name")
            @Size(max = 100)
            @Schema(description = "Quick workout adi")
            String name,

            @JsonProperty("setResults")
            @JsonAlias("set_result")
            @Valid @Size(min = 1, max = 200) @NotNull
            @Schema(description = "Kaydedilecek set sonucu listesi")
            List<SetResultDTO.CreateRequest> setResults
    ) {}

    @Schema(description = "Quick workout kaydetme yaniti")
    public record SaveQuickExerciseResponse(
            UUID workoutSessionId
    ) {}
}
