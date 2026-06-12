package com.traintension.identity.controllers;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.identity.constants.ApiPathConstants;
import com.traintension.identity.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPathConstants.ADMIN_USER)
@RequiredRole("ADMIN")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getPaged(page));
    }

    @GetMapping("/by-email")
    ResponseEntity<?> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/search/email")
    public ResponseEntity<?> searchByEmail(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(userService.searchByEmail(query, page));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable UUID id) {
        userService.restoreUser(id);
        return ResponseEntity.ok().build();
    }
}

