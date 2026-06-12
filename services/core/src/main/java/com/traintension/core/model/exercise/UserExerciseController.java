package com.traintension.core.model.exercise;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.exercise.ExerciseDTO.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(ApiConstants.EXERCISE)
@RequiredArgsConstructor
@RequiredRole("USER")
@Tag(name = "Exercise", description = "Kullanıcıya özel (personal) egzersiz yönetimi.")
public class UserExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping
    @Operation(
            summary = "Kişisel egzersiz oluştur",
            description = "Kullanıcıya özel birden fazla egzersiz oluşturur. `items` dizisi içinde 1-200 arası egzersiz gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersizler başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> create(@Valid @RequestBody CreatePersonalsRequest request) {
        return ResponseEntity.ok(exerciseService.createAll(request, UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Kişisel egzersiz güncelle",
            description = "Kullanıcıya ait egzersizleri kısmi olarak günceller. Sadece gönderilen alanlar güncellenir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersizler başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla egzersiz bulunamadı")
    })
    public ResponseEntity<List<Response>> update(@Valid @RequestBody UpdatePersonalsRequest request) {
        return ResponseEntity.ok(exerciseService.updateAll(request, UserContext.getId()));
    }


    @DeleteMapping
    @Operation(
            summary = "Kişisel egzersiz sil",
            description = "Kullanıcıya ait egzersizleri siler. Sadece kullanıcının kendi egzersizleri silinebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen egzersiz UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla egzersiz bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteByIds(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(exerciseService.deleteByIds(UserContext.getId(), ids));
    }

    @PostMapping("/{id}/favorite")
    @Operation(
            summary = "Egzersiz favori durumunu ayarla",
            description = "Global veya kullaniciya ait bir egzersizi favoriye ekler ya da favoriden kaldirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favori durumu guncellendi"),
            @ApiResponse(responseCode = "400", description = "Favori limiti asildi veya egzersiz zaten favori"),
            @ApiResponse(responseCode = "404", description = "Egzersiz veya favori kaydi bulunamadi")
    })
    public ResponseEntity<Void> setFavorite(
            @PathVariable UUID id,
            @Valid @RequestBody SetFavoriteRequest request) {
        exerciseService.setFavorite(id, request.favorite(), UserContext.getId());
        return ResponseEntity.ok().build();
    }
}
