package com.traintension.identity.controllers;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.identity.constants.ApiPathConstants;
import com.traintension.identity.dto.GoogleTokenRequestDTO;
import com.traintension.identity.dto.RefreshTokenDTO;
import com.traintension.identity.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPathConstants.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/google")
    @RequiredRole("ANONYMOUS")
    public ResponseEntity<?> googleAuth(@RequestBody @Valid GoogleTokenRequestDTO dto) {
        return ResponseEntity.ok(authService.googleAuth(dto));
    }

    @PostMapping("/refresh")
    @RequiredRole("ANONYMOUS")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshTokenDTO dto) {
        return ResponseEntity.ok(authService.refresh(dto.refreshToken()));
    }

    @PostMapping("/logout")
    @RequiredRole("ANONYMOUS")
    public ResponseEntity<?> logout(@RequestBody @Valid RefreshTokenDTO dto) {
        authService.logout(dto.refreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout/all")
    @RequiredRole({"USER", "COACH", "ADMIN"})
    public ResponseEntity<?> logoutAll() {
        authService.logoutAll(UserContext.getId());
        return ResponseEntity.ok().build();
    }
}
