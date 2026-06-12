package com.traintension.core.model.exerciseMuscle;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.exerciseMuscle.ExerciseMuscleDTO.*;
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
@RequiredArgsConstructor
@RequestMapping(ApiConstants.EXERCISE_MUSCLE)
@RequiredRole("USER")
@Tag(name = "Exercise Muscle", description = "Kullanıcıya özel egzersiz-kas ilişkisi yönetimi.")
public class UserExerciseMuscleController {
    private final ExerciseMuscleService exerciseMuscleService;

    @PostMapping
    @Operation(
            summary = "Kişisel egzersiz-kas ilişkisi oluştur",
            description = "Kullanıcıya özel egzersiz-kas bağlantıları oluşturur. `items` dizisi içinde 1-100 arası bağlantı gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İlişkiler başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createAll(@RequestBody @Valid CreatePersonalsRequest request) {
        return ResponseEntity.ok(exerciseMuscleService.createAll(request, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Kişisel egzersiz-kas ilişkisi sil",
            description = "Kullanıcıya ait egzersiz-kas ilişkilerini siler. Sadece kullanıcının kendi ilişkileri silinebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen ilişki UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ilişki bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteAll(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(exerciseMuscleService.deleteAll(ids, UserContext.getId()));
    }
}
