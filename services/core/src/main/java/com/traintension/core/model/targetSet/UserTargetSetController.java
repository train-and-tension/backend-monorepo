package com.traintension.core.model.targetSet;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.TARGET_SET)
@Validated
@RequiredRole("USER")
@Tag(name = "Target Set", description = """
        Kullanıcıya özel hedef set yönetimi. Bir target set, bir egzersizin belirli bir antrenman günündeki set detaylarını tanımlar \
        (tekrar sayısı, ağırlık, süre, dinlenme süresi, sıralama).""")
public class UserTargetSetController {
    private final TargetSetService targetSetService;

    @PostMapping
    @Operation(
            summary = "Kişisel hedef set oluştur",
            description = "Belirtilen workout program ve workout day altında kullanıcıya özel hedef setler oluşturur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler oluşturuldu, güncellenmiş workout day yanıtı döndü"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Workout program veya workout day bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> createPersonalTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.CreatePersonalsRequest request) {
        return ResponseEntity.ok(targetSetService.createPersonalTargetSets(request, workoutProgramId, workoutDayId, UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Kişisel hedef set güncelle",
            description = "Kullanıcıya ait hedef setleri kısmi olarak günceller. Sadece gönderilen alanlar güncellenir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> updatePersonalTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.UpdatePersonalsRequest request) {
        return ResponseEntity.ok(targetSetService.updatePersonalTargetSets(request, workoutProgramId, workoutDayId, UserContext.getId()));
    }

    @PutMapping("/reorder")
    @Operation(
            summary = "Kişisel hedef set sıralamasını değiştir",
            description = "Kullanıcıya ait hedef setlerin sıralamasını değiştirir. Her kayıt için `id` ve yeni `orderNumber` gönderilmelidir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sıralama başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - tekrar eden order number veya validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> reorderPersonalTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId,
            @Valid @RequestBody TargetSetDTO.ReorderPersonalsRequest request) {
        return ResponseEntity.ok(targetSetService.reorderPersonalTargetSets(request, workoutProgramId, workoutDayId, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Kişisel hedef set sil",
            description = "Kullanıcıya ait hedef setleri siler. Sadece kullanıcının kendi hedef setleri silinebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef setler başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<Void> deletePersonalTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Silinecek hedef set UUID'leri", required = true)
            @RequestParam @NotEmpty Set<UUID> ids,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId) {
        targetSetService.deletePersonalTargetSets(workoutProgramId, workoutDayId, ids, UserContext.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(
            summary = "Workout day'e göre kişisel hedef setleri getir",
            description = "Belirtilen workout program ve workout day altındaki kullanıcıya ait hedef setleri sıralı olarak getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Workout program veya workout day bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutDay> getPersonalTargetSets(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId,
            @Parameter(description = "Antrenman günü UUID'si", required = true)
            @RequestParam UUID workoutDayId) {
        return ResponseEntity.ok(targetSetService.getPersonalTargetSetsByWorkoutDay(workoutProgramId, workoutDayId, UserContext.getId()));
    }

    @GetMapping("/by-workout-program")
    @Operation(
            summary = "Workout program'a göre tüm kişisel hedef setleri getir",
            description = "Belirtilen workout program altındaki tüm workout day'lerin kullanıcıya ait hedef setlerini gruplandırılmış olarak getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout day bazında gruplandırılmış hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Workout program bulunamadı")
    })
    public ResponseEntity<ResponsesByWorkoutProgram> getPersonalTargetSetsByWorkoutProgram(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @RequestParam UUID workoutProgramId) {
        return ResponseEntity.ok(targetSetService.getPersonalTargetSetsByWorkoutProgram(workoutProgramId, UserContext.getId()));
    }

    @GetMapping("/by-ids")
    @Operation(
            summary = "ID'lere göre kişisel hedef setleri getir",
            description = "Belirtilen UUID'lere sahip kullanıcıya ait hedef setleri getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hedef set listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla hedef set bulunamadı")
    })
    public ResponseEntity<List<Response>> getPersonalTargetSetsByIds(
            @Parameter(description = "Hedef set UUID'leri", required = true)
            @RequestParam @NotEmpty Set<UUID> ids) {
        return ResponseEntity.ok(targetSetService.getPersonalTargetSetsByIds(ids, UserContext.getId()));
    }
}
