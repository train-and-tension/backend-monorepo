package com.traintension.core.model.muscle;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.muscle.MuscleDTO.*;
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
@RequestMapping(ApiConstants.ADMIN_MUSCLE)
@RequiredArgsConstructor
@RequiredRole("ADMIN")
@Tag(name = "Admin - Muscle", description = "Sistem geneli kas (muscle) yönetimi. Sadece ADMIN rolü erişebilir.")
public class AdminMuscleController {
    private final MuscleService muscleService;

    @PostMapping
    @Operation(
            summary = "Toplu kas oluştur",
            description = "Birden fazla kas tanımı oluşturur. `items` dizisi içinde 1-500 arası kas gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kaslar başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createAll(@Valid @RequestBody CreateGlobalsRequest request) {
        return ResponseEntity.ok(muscleService.createAll(request));
    }

    @PatchMapping
    @Operation(
            summary = "Toplu kas güncelle",
            description = "Birden fazla kas tanımını kısmi olarak günceller. Sadece gönderilen alanlar güncellenir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kaslar başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla kas bulunamadı")
    })
    public ResponseEntity<List<Response>> update(@RequestBody @Valid UpdateGlobalsRequest request) {
        return ResponseEntity.ok(muscleService.updateAll(request));
    }


    @DeleteMapping
    @Operation(
            summary = "Toplu kas sil",
            description = "Belirtilen UUID'lere sahip kas tanımlarını siler."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen kas UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla kas bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteMuscles(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(muscleService.deleteByIds(ids));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile kas getir",
            description = "Belirtilen UUID'ye sahip kası getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kas bulundu"),
            @ApiResponse(responseCode = "404", description = "Kas bulunamadı")
    })
    public ResponseEntity<Response> getById(
            @Parameter(description = "Kas UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(muscleService.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Tüm kasları listele",
            description = "Sistemdeki tüm kas tanımlarını getirir."
    )
    @ApiResponse(responseCode = "200", description = "Kas listesi döndü")
    public ResponseEntity<List<Response>> getAll() {
        return ResponseEntity.ok(muscleService.getAll());
    }
}
