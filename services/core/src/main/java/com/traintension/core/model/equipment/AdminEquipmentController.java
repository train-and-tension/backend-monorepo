package com.traintension.core.model.equipment;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.equipment.EquipmentDTO.*;
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
@RequestMapping(ApiConstants.ADMIN_EQUIPMENT)
@RequiredArgsConstructor
@RequiredRole("ADMIN")
@Tag(name = "Admin - Equipment", description = "Sistem geneli ekipman (equipment) yönetimi. Sadece ADMIN rolü erişebilir.")
public class AdminEquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    @Operation(
            summary = "Toplu ekipman oluştur",
            description = "Birden fazla global ekipmanı tek seferde oluşturur. `items` dizisi içinde 1-500 arası ekipman gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ekipmanlar başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createEquipment(@Valid @RequestBody CreateGlobalsRequest request) {
        return ResponseEntity.ok(equipmentService.createAll(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile ekipman getir",
            description = "Belirtilen UUID'ye sahip ekipmanı getirir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ekipman bulundu"),
            @ApiResponse(responseCode = "404", description = "Ekipman bulunamadı")
    })
    public ResponseEntity<Response> getEquipment(
            @Parameter(description = "Ekipman UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(equipmentService.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Tüm ekipmanları listele",
            description = "Sistemdeki tüm global ekipmanları getirir."
    )
    @ApiResponse(responseCode = "200", description = "Ekipman listesi döndü")
    public ResponseEntity<List<Response>> getEquipments() {
        return ResponseEntity.ok(equipmentService.findAll());
    }

    @PatchMapping
    @Operation(
            summary = "Toplu ekipman güncelle",
            description = "Birden fazla global ekipmanı kısmi olarak günceller. Sadece gönderilen alanlar güncellenir. Her kayıt için `id` zorunludur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ekipmanlar başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ekipman bulunamadı")
    })
    public ResponseEntity<List<Response>> updateAll(@RequestBody @Valid UpdateGlobalsRequest request) {
        return ResponseEntity.ok(equipmentService.updateAll(request));
    }

    @DeleteMapping
    @Operation(
            summary = "Toplu ekipman sil",
            description = "Belirtilen UUID'lere sahip ekipmanları siler. Request body'de UUID seti gönderilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen ekipman UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ekipman bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteEquipment(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(equipmentService.deleteEquipments(ids));
    }
}
