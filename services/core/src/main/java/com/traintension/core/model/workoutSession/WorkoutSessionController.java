package com.traintension.core.model.workoutSession;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.workoutSession.WorkoutSessionDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.WORKOUT_SESSION)
@RequiredRole("USER")
@RequiredArgsConstructor
@Tag(name = "Workout Session", description = "Kullaniciya ait workout session islemleri. Quick workout kaydi bu controller uzerinden yapilir.")
public class WorkoutSessionController {
    private final WorkoutSessionService workoutSessionService;

    @PostMapping("/quick-workout")
    @Operation(
            summary = "Quick workout kaydet",
            description = "Aktif workout period icindeki kullanici icin FINISHED durumunda QUICK_WORKOUT session olusturur ve gonderilen set sonuclarini bu session'a baglar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quick workout kaydedildi ve workout session UUID'si donduruldu"),
            @ApiResponse(responseCode = "400", description = "Gecersiz istek - validation hatasi"),
            @ApiResponse(responseCode = "404", description = "Aktif workout period veya kullanilabilir egzersiz bulunamadi")
    })
    public ResponseEntity<SaveQuickExerciseResponse> saveQuickExercise(@Valid @RequestBody SaveQuickExerciseRequest request) {
        return ResponseEntity.ok(workoutSessionService.saveQuickExercise(request, UserContext.getId()));
    }
}
