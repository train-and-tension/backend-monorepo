package com.traintension.core.model.userProfile;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.userProfile.UserProfileDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequiredRole({"USER"})
@RequestMapping(ApiConstants.USER_PROFILE)
@Tag(name = "User Profile", description = "Kullanıcı profil yönetimi (ad, soyad, kullanıcı adı, saat dilimi, profil fotoğrafı).")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping
    @Operation(
            summary = "Kendi profilini getir",
            description = "Oturum açmış kullanıcının kendi profil bilgilerini getirir."
    )
    @ApiResponse(responseCode = "200", description = "Profil bilgileri döndü")
    public ResponseEntity<Response> getProfile() {
        return ResponseEntity.ok(userProfileService.getById(UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Profil bilgilerini güncelle",
            description = "Kullanıcı profilini kısmi olarak günceller. Sadece gönderilen alanlar güncellenir (firstName, lastName, profilePicUrl)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek - validation hatası")
    })
    public ResponseEntity<Response> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(request, UserContext.getId()));
    }

    @PutMapping("/timezone")
    @Operation(
            summary = "Saat dilimini değiştir",
            description = "Kullanıcının saat dilimini günceller. Geçerli bir IANA timezone (örn: 'Europe/Istanbul', 'America/New_York') gönderilmelidir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saat dilimi başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz saat dilimi formatı")
    })
    public ResponseEntity<TimezoneResponse> updateTimezone(@Valid @RequestBody ChangeTimezoneRequest request) {
        return ResponseEntity.ok(userProfileService.updateTimezone(request, UserContext.getId()));
    }

    @PutMapping("/username")
    @Operation(
            summary = "Kullanıcı adını değiştir",
            description = "Kullanıcının kullanıcı adını değiştirir. Kullanıcı adı 8-20 karakter arasında olmalı ve benzersiz olmalıdır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı adı başarıyla değiştirildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz kullanıcı adı formatı"),
            @ApiResponse(responseCode = "409", description = "Kullanıcı adı zaten kullanımda")
    })
    public ResponseEntity<UsernameResponse> updateUsername(@Valid @RequestBody ChangeUsernameRequest request) {
        return ResponseEntity.ok(userProfileService.updateUsername(request, UserContext.getId()));
    }

    @GetMapping("/username/available")
    @Operation(
            summary = "Kullanıcı adı müsaitliğini kontrol et",
            description = "Belirtilen kullanıcı adının kullanılabilir olup olmadığını kontrol eder. `true` dönerse kullanıcı adı müsaittir."
    )
    @ApiResponse(responseCode = "200", description = "Müsaitlik durumu döndü (true=müsait, false=meşgul)")
    public ResponseEntity<Boolean> isUsernameAvailable(
            @Parameter(description = "Kontrol edilecek kullanıcı adı (8-20 karakter)", required = true)
            @RequestParam @Size(min = 8, max = 20) String username) {
        return ResponseEntity.ok(userProfileService.isUsernameAvailable(username));
    }
}
