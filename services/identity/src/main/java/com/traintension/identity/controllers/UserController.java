package com.traintension.identity.controllers;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.identity.constants.ApiPathConstants;
import com.traintension.identity.dto.ChangeEmailRequest;
import com.traintension.identity.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPathConstants.USER)
@RequiredRole("USER")
public class UserController {
    private final UserService userService;

    @PutMapping("/email")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailRequest request) {
        return ResponseEntity.ok().body(userService.changeEmail(
                UserContext.getId(),
                UserContext.getEmail(),
                request
        ));
    }
}
