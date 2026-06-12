package com.traintension.core.model.exercise;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.exercise.ExerciseDTO.*;
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
@RequestMapping(ApiConstants.ADMIN_EXERCISE)
@RequiredRole("ADMIN")
@Tag(name = "Admin - Exercise", description = "Sistem geneli egzersiz yönetimi. Sadece ADMIN rolü erişebilir.")
public class AdminExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping
    @Operation(
            summary = "Toplu egzersiz oluştur",
            description = "Birden fazla global egzersiz oluşturur. `items` dizisi içinde 1-200 arası egzersiz gönderilebilir. Her egzersiz opsiyonel olarak bir ekipmana (`equipmentId`) bağlanabilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersizler başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> create(@Valid @RequestBody CreateGlobalsRequest request) {
        return ResponseEntity.ok(exerciseService.createAll(request));
    }

    @PatchMapping
    @Operation(
            summary = "Toplu egzersiz güncelle",
            description = "Birden fazla global egzersizi kısmi olarak günceller. Sadece gönderilen alanlar güncellenir. Her kayıt için `id` zorunludur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersizler başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla egzersiz bulunamadı")
    })
    public ResponseEntity<List<Response>> update(@Valid @RequestBody UpdateGlobalsRequest request) {
        return ResponseEntity.ok(exerciseService.updateAll(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile egzersiz getir",
            description = "Belirtilen UUID'ye sahip egzersizi getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Egzersiz bulundu"),
            @ApiResponse(responseCode = "404", description = "Egzersiz bulunamadı")
    })
    public ResponseEntity<Response> getById(
            @Parameter(description = "Egzersiz UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(exerciseService.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Tüm egzersizleri listele",
            description = "Sistemdeki tüm global egzersizleri getirir."
    )
    @ApiResponse(responseCode = "200", description = "Egzersiz listesi döndü")
    public ResponseEntity<List<Response>> getAll() {
        return ResponseEntity.ok(exerciseService.findAll());
    }

    @DeleteMapping
    @Operation(
            summary = "Toplu egzersiz sil",
            description = "Belirtilen UUID'lere sahip egzersizleri siler. Request body'de UUID seti gönderilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen egzersiz UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla egzersiz bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteByIds(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(exerciseService.deleteByIds(ids));
    }
}
