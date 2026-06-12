package com.traintension.identity.config.interfaces;

import com.traintension.common.dto.CreateUserProfileRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

public interface UserProfileClient {

    @PostExchange("/inline/api/core/user-profile")
    void createUserProfile(@RequestBody CreateUserProfileRequest request);

    @DeleteExchange("/inline/api/core/user-profile/{id}")
    void deleteUserProfile(@PathVariable UUID id);

    @GetExchange("/inline/api/core/user-profile/{id}/exists")
    Boolean existsByUserId(@PathVariable UUID id);
}
