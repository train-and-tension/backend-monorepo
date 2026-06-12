package com.traintension.core.model.workoutProgram;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.workoutProgram.WorkoutProgramDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(ApiConstants.WORKOUT_PROGRAM)
@RequiredArgsConstructor
@RequiredRole("USER")
@Tag(name = "Workout Program", description = "Kullanıcıya özel antrenman programı yönetimi. Bir workout program, birden fazla antrenman gününü kapsayan üst seviye yapıdır.")
public class UserWorkoutProgramController {
    private final WorkoutProgramService workoutProgramService;

    @PostMapping
    @Operation(
            summary = "Kişisel antrenman programı oluştur",
            description = "Kullanıcıya özel antrenman programları oluşturur. `items` dizisi içinde 1-20 arası program gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Programlar başarıyla oluşturuldu",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> create(@Valid @RequestBody CreatePersonalsRequest request) {
        return ResponseEntity.ok(workoutProgramService.createAll(request, UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Kişisel antrenman programı güncelle",
            description = "Kullanıcıya ait programları kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (name, description)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Programlar başarıyla güncellendi",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla program bulunamadı")
    })
    public ResponseEntity<List<Response>> update(@Valid @RequestBody UpdatePersonalsRequest request) {
        return ResponseEntity.ok(workoutProgramService.updateAll(request, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Kişisel antrenman programı sil",
            description = "Kullanıcıya ait antrenman programlarını siler. Programla birlikte ilişkili workout day'ler ve target set'ler de silinir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Programlar başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla program bulunamadı")
    })
    public ResponseEntity<Void> deleteByIds(@RequestBody Set<UUID> ids) {
        workoutProgramService.deleteByIds(UserContext.getId(), ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/duplicate")
    @Operation(
            summary = "Antrenman programini kopyala",
            description = "Sistem programini gunleri ve hedef setleriyle birlikte kullaniciya kopyalar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Program basariyla kopyalandi"),
            @ApiResponse(responseCode = "404", description = "Program bulunamadi")
    })
    public ResponseEntity<DuplicateResponse> duplicate(
            @Parameter(description = "Kopyalanacak program UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(workoutProgramService.duplicateWorkoutProgramForUser(id, UserContext.getId()));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(
            summary = "Antrenman programını deaktif et",
            description = "Belirtilen antrenman programını pasif duruma geçirir. Deaktif edilen program artık aktif antrenman akışında kullanılmaz."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Program başarıyla deaktif edildi"),
            @ApiResponse(responseCode = "404", description = "Program bulunamadı")
    })
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "Deaktif edilecek program UUID'si", required = true)
            @PathVariable UUID id) {
        workoutProgramService.deactivateWorkoutProgram(id, UserContext.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    @Operation(
            summary = "Antrenman programini aktiflestir",
            description = "Belirtilen global veya kullaniciya ait programi gelecek bir baslangic tarihiyle aktif antrenman akisina alir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Program basariyla aktiflestirildi"),
            @ApiResponse(responseCode = "400", description = "Gecersiz istek veya program aktiflestirme kosullari saglanmadi"),
            @ApiResponse(responseCode = "404", description = "Program bulunamadi")
    })
    public ResponseEntity<Void> activate(
            @Parameter(description = "Aktiflestirilecek program UUID'si", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ActivateRequest request) {
        workoutProgramService.activateWorkoutProgram(id, request.startDate(), UserContext.getId());
        return ResponseEntity.ok().build();
    }
}
