package com.traintension.core.model.workoutDay;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.workoutDay.WorkoutDayDTO.*;
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
@RequestMapping(ApiConstants.ADMIN_WORKOUT_DAY)
@RequiredArgsConstructor
@RequiredRole("ADMIN")
@Tag(name = "Admin - Workout Day", description = "Sistem geneli antrenman günü yönetimi. Bir workout day, bir workout program altındaki belirli bir antrenman gününü temsil eder. Sadece ADMIN rolü erişebilir.")
public class AdminWorkoutDayController {
    private final WorkoutDayService workoutDayService;

    @PostMapping("/{workoutProgramId}")
    @Operation(
            summary = "Toplu antrenman günü oluştur",
            description = "Belirtilen workout program altında birden fazla global antrenman günü oluşturur. `items` dizisi içinde 1-500 arası gün gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Antrenman günleri oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Workout program bulunamadı")
    })
    public ResponseEntity<Responses> createAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @Valid @RequestBody CreateGlobalsRequest request
    ) {
        return ResponseEntity.ok(workoutDayService.createGlobalWorkoutDays(request, workoutProgramId));
    }

    @DeleteMapping("/{workoutProgramId}")
    @Operation(
            summary = "Toplu antrenman günü sil",
            description = "Belirtilen workout program altındaki antrenman günlerini siler. Body'de silinecek UUID seti gönderilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen gün UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @RequestBody Set<UUID> ids
    ) {
        return ResponseEntity.ok(
                workoutDayService.deleteGlobalWorkoutDays(workoutProgramId, ids)
        );
    }

    @PutMapping("/{workoutProgramId}/reorder")
    @Operation(
            summary = "Antrenman günü sıralamasını değiştir",
            description = "Global antrenman günlerinin sıralamasını değiştirir. Her kayıt için `id` ve yeni `orderNumber` gönderilmelidir. Order number'lar unique olmalıdır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sıralama başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - tekrar eden order number veya validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<Responses> reorderWorkoutDays(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @RequestBody @Valid ReorderGlobalsRequest request
    ) {
        return ResponseEntity.ok(workoutDayService.reorderGlobalWorkoutDays(request, workoutProgramId));
    }

    @GetMapping
    @Operation(
            summary = "ID'lere göre antrenman günlerini getir",
            description = "Belirtilen UUID'lere sahip antrenman günlerini getirir. Body'de UUID seti gönderilir."
    )
    @ApiResponse(responseCode = "200", description = "Antrenman günü listesi döndü")
    public ResponseEntity<List<Response>> getWorkoutDaysByIds(@RequestBody Set<UUID> workoutDayIds) {
        return ResponseEntity.ok(workoutDayService.getWorkoutsDayByIds(workoutDayIds));
    }

    @PatchMapping("/{workoutProgramId}")
    @Operation(
            summary = "Toplu antrenman günü güncelle",
            description = "Birden fazla global antrenman gününü kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (name, isOff)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Antrenman günleri güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<Responses> updateAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @Valid @RequestBody UpdateGlobalsRequest request
    ) {
        return ResponseEntity.ok(workoutDayService.updateGlobalWorkoutDays(request, workoutProgramId));
    }


    @GetMapping("/{workoutProgramId}")
    @Operation(
            summary = "Program'a göre tüm antrenman günlerini getir",
            description = "Belirtilen workout program altındaki tüm global antrenman günlerini sıralı olarak getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Antrenman günü listesi döndü"),
            @ApiResponse(responseCode = "404", description = "Workout program bulunamadı")
    })
    public ResponseEntity<Responses> getAllByWorkoutProgramId(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable("workoutProgramId") UUID workoutProgramId) {
        return ResponseEntity.ok(workoutDayService.getAllGlobalDaysByProgram(workoutProgramId));
    }
}
