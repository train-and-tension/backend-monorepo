package com.traintension.core.model.bodyInformation;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.bodyInformation.BodyInformationDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.BODY_INFORMATION)
@RequiredRole("USER")
@RequiredArgsConstructor
@Tag(name = "Body Information", description = "Kullanıcı vücut bilgileri yönetimi (boy, kilo, cinsiyet, hedefler vb.)")
public class BodyInformationController {
    private final BodyInformationService bodyInformationService;

    @PostMapping
    @Operation(
            summary = "Vücut bilgisi oluştur",
            description = "Kullanıcı için ilk kez vücut bilgilerini (boy, kilo, birim sistemi, doğum tarihi, cinsiyet, aktivite seviyesi, antrenman hedefi, kilo hedefi) kaydeder."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vücut bilgisi başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<Response> create(@RequestBody @Valid CreatePersonalRequest request) {
        return ResponseEntity.ok(bodyInformationService.create(request, UserContext.getId()));
    }

    @PutMapping("/measurements")
    @Operation(
            summary = "Ölçümleri güncelle",
            description = "Mevcut vücut ölçümlerini (boy, kilo, birim sistemi) günceller. Tüm ölçüm alanları zorunludur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ölçümler başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Vücut bilgisi bulunamadı")
    })
    public ResponseEntity<Response> updateMeasurements(@RequestBody @Valid UpdateMeasurementsRequest request) {
        return ResponseEntity.ok(bodyInformationService.updateMeasurements(request, UserContext.getId()));
    }

    @PatchMapping("/profile")
    @Operation(
            summary = "Profil bilgilerini güncelle",
            description = "Vücut profil bilgilerini kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (doğum tarihi, cinsiyet, aktivite seviyesi, antrenman hedefi, kilo hedefi)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil bilgileri başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası"),
            @ApiResponse(responseCode = "404", description = "Vücut bilgisi bulunamadı")
    })
    public ResponseEntity<Response> patchProfile(@RequestBody @Valid UpdateProfileRequest request) {
        return ResponseEntity.ok(bodyInformationService.updateProfile(request, UserContext.getId()));
    }
}
