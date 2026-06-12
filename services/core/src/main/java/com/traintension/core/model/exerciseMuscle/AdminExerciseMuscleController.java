package com.traintension.core.model.exerciseMuscle;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.generated.enums.ActivationLevel;
import com.traintension.core.model.exerciseMuscle.ExerciseMuscleDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(ApiConstants.ADMIN_EXERCISE_MUSCLE)
@RequiredRole("ADMIN")
@Tag(name = "Admin - Exercise Muscle", description = "Egzersiz-kas ilişkisi yönetimi. Hangi egzersizin hangi kası ne seviyede çalıştırdığını tanımlar. Sadece ADMIN rolü erişebilir.")
public class AdminExerciseMuscleController {
    private final ExerciseMuscleService exerciseMuscleService;

    @PostMapping
    @Operation(
            summary = "Toplu egzersiz-kas ilişkisi oluştur",
            description = "Birden fazla egzersiz-kas bağlantısı oluşturur. Her bağlantı için `exerciseId`, `muscleId` ve `activationLevel` zorunludur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İlişkiler başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createAll(@RequestBody @Valid CreateGlobalsRequest request) {
        return ResponseEntity.ok(exerciseMuscleService.createAll(request));
    }

    @DeleteMapping
    @Operation(
            summary = "Toplu egzersiz-kas ilişkisi sil",
            description = "Belirtilen UUID'lere sahip egzersiz-kas ilişkilerini siler."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen ilişki UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ilişki bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteAll(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(exerciseMuscleService.deleteAll(ids));
    }

    @GetMapping
    @Operation(
            summary = "Tüm egzersiz-kas ilişkilerini listele",
            description = "Sistemdeki tüm egzersiz-kas bağlantılarını getirir."
    )
    @ApiResponse(responseCode = "200", description = "İlişki listesi döndü")
    public ResponseEntity<List<Response>> getAll() {
        return ResponseEntity.ok(exerciseMuscleService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile egzersiz-kas ilişkisi getir",
            description = "Belirtilen UUID'ye sahip egzersiz-kas ilişkisini getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İlişki bulundu"),
            @ApiResponse(responseCode = "404", description = "İlişki bulunamadı")
    })
    public ResponseEntity<Response> getById(
            @Parameter(description = "İlişki UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(exerciseMuscleService.getById(id));
    }


    @GetMapping("/find-muscles-by-exercise/{exerciseId}")
    @Operation(
            summary = "Egzersize bağlı kasları getir",
            description = "Belirtilen egzersizin çalıştırdığı kasları, aktivasyon seviyesine göre gruplandırılmış olarak getirir. Opsiyonel olarak `level` parametresi ile filtrelenebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kas listesi döndü (activationLevel'a göre gruplandırılmış)"),
            @ApiResponse(responseCode = "404", description = "Egzersiz bulunamadı")
    })
    public ResponseEntity<MuscleResponse> getMusclesByExerciseId(
            @Parameter(description = "Egzersiz UUID'si", required = true)
            @PathVariable UUID exerciseId,
            @Parameter(description = "Aktivasyon seviyesi filtresi (opsiyonel). Gönderilmezse tüm seviyeler döner.")
            @RequestParam(required = false) ActivationLevel level
    ) {
        return ResponseEntity.ok(exerciseMuscleService.findMusclesByExerciseId(exerciseId, level));
    }

    @GetMapping("/find-exercises-by-muscle/{muscleId}")
    @Operation(
            summary = "Kasa bağlı egzersizleri getir",
            description = "Belirtilen kası çalıştıran egzersizleri, aktivasyon seviyesine göre gruplandırılmış olarak getirir. Opsiyonel olarak `level` parametresi ile filtrelenebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersiz listesi döndü (activationLevel'a göre gruplandırılmış)"),
            @ApiResponse(responseCode = "404", description = "Kas bulunamadı")
    })
    public ResponseEntity<ExerciseResponse> getExercisesByMuscleId(
            @Parameter(description = "Kas UUID'si", required = true)
            @PathVariable UUID muscleId,
            @Parameter(description = "Aktivasyon seviyesi filtresi (opsiyonel). Gönderilmezse tüm seviyeler döner.")
            @RequestParam(required = false) ActivationLevel level
    ) {
        return ResponseEntity.ok(exerciseMuscleService.findExercisesByMuscleId(muscleId, level));
    }
}
