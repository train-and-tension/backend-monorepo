package com.traintension.core.model.workoutDay;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
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
@RequestMapping(ApiConstants.WORKOUT_DAY)
@RequiredArgsConstructor
@RequiredRole("USER")
@Tag(name = "Workout Day", description = "Kullanıcıya özel antrenman günü yönetimi. Bir workout day, bir workout program altındaki belirli bir antrenman gününü temsil eder.")
public class UserWorkoutDayController {
    private final WorkoutDayService workoutDayService;

    @PostMapping("/{workoutProgramId}")
    @Operation(
            summary = "Kişisel antrenman günü oluştur",
            description = "Belirtilen workout program altında kullanıcıya özel antrenman günleri oluşturur. `items` dizisi içinde 1-100 arası gün gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Antrenman günleri oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Workout program bulunamadı")
    })
    public ResponseEntity<Responses> createAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @Valid @RequestBody CreatePersonalsRequest request
    ) {
        return ResponseEntity.ok(workoutDayService.createPersonalWorkoutDays(
                request, workoutProgramId, UserContext.getId())
        );
    }

    @DeleteMapping("/{workoutProgramId}")
    @Operation(
            summary = "Kişisel antrenman günü sil",
            description = "Kullanıcıya ait antrenman günlerini siler. Sadece kullanıcının kendi günleri silinebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen gün UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(
                workoutDayService.deletePersonalWorkoutDays(workoutProgramId, ids, UserContext.getId())
        );
    }

    @PutMapping("/{workoutProgramId}/reorder")
    @Operation(
            summary = "Kişisel antrenman günü sıralamasını değiştir",
            description = "Kullanıcıya ait antrenman günlerinin sıralamasını değiştirir. Her kayıt için `id` ve yeni `orderNumber` gönderilmelidir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sıralama başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - tekrar eden order number veya validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<Responses> reorderAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @RequestBody @Valid ReorderPersonalsRequest reorderPersonalsRequest
    ) {
        return ResponseEntity.ok(
                workoutDayService.reorderPersonalWorkoutDays(
                        reorderPersonalsRequest,
                        workoutProgramId,
                        UserContext.getId()
                )
        );
    }

    @PatchMapping("/{workoutProgramId}")
    @Operation(
            summary = "Kişisel antrenman günü güncelle",
            description = "Kullanıcıya ait antrenman günlerini kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (name, isOff)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Antrenman günleri güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla antrenman günü bulunamadı")
    })
    public ResponseEntity<Responses> updateAll(
            @Parameter(description = "Antrenman programı UUID'si", required = true)
            @PathVariable UUID workoutProgramId,
            @Valid @RequestBody UpdatePersonalsRequest request
    ) {
        return ResponseEntity.ok(
                workoutDayService.updatePersonalWorkoutDays(request, workoutProgramId, UserContext.getId())
        );
    }

}
