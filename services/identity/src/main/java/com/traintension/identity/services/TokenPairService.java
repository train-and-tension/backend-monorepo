package com.traintension.identity.services;

import com.traintension.identity.dto.TokenPairDTO;
import com.traintension.identity.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenPairService {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public TokenPairDTO generate(User user) {
        return new TokenPairDTO(
                jwtService.generateToken(
                        user.getId(),
                        user.getEmail(),
                        user.getRoles()
                ),
                refreshTokenService.create(user.getId())
        );
    }
}
