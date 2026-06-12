package com.traintension.core.model.userProfile;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.userProfile.UserProfileDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequiredRole("ADMIN")
@RequestMapping(ApiConstants.ADMIN_USER_PROFILE)
@Tag(name = "Admin - User Profile", description = "Admin tarafından kullanıcı profili sorgulama. Sadece ADMIN rolü erişebilir.")
public class AdminUserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    @Operation(
            summary = "ID ile kullanıcı profili getir",
            description = "Belirtilen UUID'ye sahip kullanıcı profilini getirir. Admin, herhangi bir kullanıcının profilini görüntüleyebilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı profili bulundu"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı profili bulunamadı")
    })
    public ResponseEntity<Response> getProfilesByUserIds(
            @Parameter(description = "Kullanıcı profili UUID'si", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(userProfileService.getById(id));
    }
}
