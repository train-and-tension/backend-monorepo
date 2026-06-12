package com.traintension.core.model.workoutProgram;

import com.traintension.common.utils.user.RequiredRole;
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
@RequestMapping(ApiConstants.ADMIN_WORKOUT_PROGRAM)
@RequiredArgsConstructor
@RequiredRole("ADMIN")
@Tag(name = "Admin - Workout Program", description = "Sistem geneli antrenman programı yönetimi. Bir workout program, birden fazla antrenman gününü kapsayan üst seviye yapıdır. Sadece ADMIN rolü erişebilir.")
public class AdminWorkoutProgramController {
    private final WorkoutProgramService workoutProgramService;

    @PostMapping
    @Operation(
            summary = "Toplu antrenman programı oluştur",
            description = "Birden fazla global antrenman programı oluşturur. `items` dizisi içinde 1-300 arası program gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Programlar başarıyla oluşturuldu",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createAll(@Valid @RequestBody CreateGlobalsRequest request) {
        return ResponseEntity.ok(workoutProgramService.createAll(request));
    }

    @PatchMapping
    @Operation(
            summary = "Toplu antrenman programı güncelle",
            description = "Birden fazla global antrenman programını kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (name, description)."
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
    public ResponseEntity<List<Response>> updateAll(@Valid @RequestBody UpdateGlobalsRequest request) {
        return ResponseEntity.ok(workoutProgramService.updateAll(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile antrenman programı getir",
            description = "Belirtilen UUID'ye sahip antrenman programını getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Program bulundu"),
            @ApiResponse(responseCode = "404", description = "Program bulunamadı")
    })
    public ResponseEntity<Response> getById(
            @Parameter(description = "Program UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(workoutProgramService.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Tüm antrenman programlarını listele",
            description = "Sistemdeki tüm global antrenman programlarını getirir."
    )
    @ApiResponse(responseCode = "200", description = "Program listesi döndü")
    public ResponseEntity<List<Response>> getAll() {
        return ResponseEntity.ok(workoutProgramService.getAll());
    }

    @DeleteMapping
    @Operation(
            summary = "Toplu antrenman programı sil",
            description = "Belirtilen UUID'lere sahip antrenman programlarını siler. Programla birlikte ilişkili workout day'ler ve target set'ler de silinir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Programlar başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla program bulunamadı")
    })
    public ResponseEntity<Void> deleteByIds(@RequestBody Set<UUID> ids) {
        workoutProgramService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }
}
