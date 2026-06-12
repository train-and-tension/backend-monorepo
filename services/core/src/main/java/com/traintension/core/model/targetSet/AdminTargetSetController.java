package com.traintension.core.model.targetSet;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.targetSet.TargetSetDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.ADMIN_TARGET_SET)
@RequiredRole("ADMIN")
@Tag(name = "Admin - Target Set", description = """
        Sistem geneli hedef set (target set) yönetimi. Bir target set, bir egzersizin belirli bir antrenman günündeki set detaylarını tanımlar \
        (tekrar sayısı, ağırlık, süre, dinlenme süresi, sıralama). Sadece ADMIN rolü erişebilir.""")
public class AdminTargetSetController {
    private final TargetSetService targetSetService;

    @PostMapping
    @Operation(
            summary = "Toplu hedef set oluştur",
            description = "Belirtilen workout program ve workout day altında birden fazla global hedef set oluşturur. `workoutProgramId` ve `workoutDayId` query parametresi olarak gönderilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler oluşturuldu, güncellenmiş workout day yanıtı döndü"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Workout program veya workout day bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> createSystemTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.CreateGlobalsRequest request) {
        return ResponseEntity.ok(targetSetService.createGlobalTargetSets(request, workoutProgramId, workoutDayId));
    }

    @PatchMapping
    @Operation(
            summary = "Toplu hedef set güncelle",
            description = "Mevcut global hedef setleri kısmi olarak günceller. Sadece gönderilen alanlar güncellenir. Her kayıt için `id` zorunludur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> updateSystemTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.UpdateGlobalsRequest request) {
        return ResponseEntity.ok(targetSetService.updateGlobalTargetSets(request, workoutProgramId, workoutDayId));
    }

    @PutMapping("/reorder")
    @Operation(
            summary = "Hedef set sıralamasını değiştir",
            description = "Global hedef setlerin sıralamasını değiştirir. Her kayıt için `id` ve yeni `orderNumber` gönderilmelidir. Order number'lar unique olmalıdır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sıralama başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - tekrar eden order number veya validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> reorderSystemTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.ReorderGlobalsRequest request) {
        return ResponseEntity.ok(targetSetService.reorderGlobalTargetSets(request, workoutProgramId, workoutDayId));
    }

    @DeleteMapping
    @Operation(
            summary = "Toplu hedef set sil",
            description = "Belirtilen UUID'lere sahip global hedef setleri siler."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<Void> deleteSystemTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Silinecek hedef set UUID'leri", required = true)
            @RequestParam @NotEmpty Set<UUID> ids,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId) {
        targetSetService.deleteGlobalTargetSets(workoutProgramId, workoutDayId, ids);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(
            summary = "Workout day'e göre hedef setleri getir",
            description = "Belirtilen workout program ve workout day altındaki tüm global hedef setleri sıralı olarak getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Workout program veya workout day bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> getSystemTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId) {
        return ResponseEntity.ok(targetSetService.getGlobalTargetSetsByWorkoutDay(workoutProgramId, workoutDayId));
    }

    @GetMapping("/by-workout-program")
    @Operation(
            summary = "Workout program'a göre tüm hedef setleri getir",
            description = "Belirtilen workout program altındaki tüm workout day'lerin hedef setlerini gruplandırılmış olarak getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout day bazında gruplandırılmış hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Workout program bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutProgram> getSystemTargetSetsByWorkoutProgram(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId) {
        return ResponseEntity.ok(targetSetService.getGlobalTargetSetsByWorkoutProgram(workoutProgramId));
    }

    @GetMapping("/by-ids")
    @Operation(
            summary = "ID'lere göre hedef setleri getir",
            description = "Belirtilen UUID'lere sahip global hedef setleri getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<List<Response>> getSystemTargetSetsByIds(
            @Parameter(description = "Hedef set UUID'leri", required = true)
            @RequestParam @NotEmpty Set<UUID> ids) {
        return ResponseEntity.ok(targetSetService.getGlobalTargetSetsByIds(ids));
    }
}
