package com.traintension.core.model.equipment;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.equipment.EquipmentDTO.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(ApiConstants.EQUIPMENT)
@RequiredArgsConstructor
@RequiredRole("USER")
@Tag(name = "Equipment", description = "Kullanıcıya özel (personal) ekipman yönetimi.")
public class UserEquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    @Operation(
            summary = "Kişisel ekipman oluştur",
            description = "Kullanıcıya özel birden fazla ekipman oluşturur. `items` dizisi içinde 1-100 arası ekipman gönderilebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ekipmanlar başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<List<Response>> createAll(@RequestBody @Valid CreatePersonalsRequest request) {
        return ResponseEntity.ok(equipmentService.createAll(request, UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Kişisel ekipman güncelle",
            description = "Kullanıcıya ait ekipmanları kısmi olarak günceller. Sadece gönderilen alanlar güncellenir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ekipmanlar başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ekipman bulunamadı")
    })
    public ResponseEntity<List<Response>> updateAll(@RequestBody @Valid UpdatePersonalsRequest request) {
        return ResponseEntity.ok(equipmentService.updateAll(request, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Kişisel ekipman sil",
            description = "Kullanıcıya ait ekipmanları siler. Sadece kullanıcının kendi ekipmanları silinebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen ekipman UUID'leri döndü"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla ekipman bulunamadı")
    })
    public ResponseEntity<List<UUID>> deleteAll(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(equipmentService.deleteEquipments(ids, UserContext.getId()));
    }
}
