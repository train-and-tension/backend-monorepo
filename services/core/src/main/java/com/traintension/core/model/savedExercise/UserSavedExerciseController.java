package com.traintension.core.model.savedExercise;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.savedExercise.SavedExerciseDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.SAVED_EXERCISE)
@RequiredRole("USER")
@Tag(name = "Saved Exercise", description = "Kullanıcının favori/kayıtlı egzersizlerini yönetimi. Kullanıcı mevcut egzersizleri kaydedebilir ve silebilir.")
public class UserSavedExerciseController {
    private final SavedExerciseService savedExerciseService;

    @PostMapping
    @Operation(
            summary = "Egzersiz kaydet",
            description = "Kullanıcının favori listesine birden fazla egzersiz ekler. `items` dizisi içinde 1-200 arası egzersiz ID'si gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Egzersizler başarıyla kaydedildi",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "409", description = "Egzersiz zaten kayıtlı")
    })
    public ResponseEntity<List<Response>> createAll(@RequestBody @Valid CreatePersonalsRequest request) {
        return ResponseEntity.ok(savedExerciseService.createAll(request, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Kayıtlı egzersiz sil",
            description = "Kullanıcının favori listesinden egzersizleri kaldırır. Body'de egzersiz UUID seti gönderilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen egzersiz UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla kayıtlı egzersiz bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteByIds(@RequestBody Set<UUID> exerciseIds) {
        return ResponseEntity.ok(savedExerciseService.deleteByIds(exerciseIds, UserContext.getId()));
    }
}
