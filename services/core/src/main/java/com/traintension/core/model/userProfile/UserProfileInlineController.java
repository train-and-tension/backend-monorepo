package com.traintension.core.model.userProfile;

import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.core.model.userProfile.UserProfileDTO.Response;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/inline/api/core/user-profile")
@RequiredArgsConstructor
@Hidden
public class UserProfileInlineController {
    private final UserProfileService userProfileService;

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(userProfileService.existsById(id));
    }

    @PostMapping
    public ResponseEntity<Response> createUserProfile(@RequestBody CreateUserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.createUserProfile(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable UUID id) {
        userProfileService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
