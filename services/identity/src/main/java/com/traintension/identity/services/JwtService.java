package com.traintension.identity.services;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.traintension.common.exception.custom.InternalServerErrorException;
import com.traintension.common.utils.user.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {
    @Value("${app.security.jwt.private-key}")
    private String privateKeyBase64;

    @Value("${app.security.jwt.expiration}")
    private long expiration;

    private ECPrivateKey privateKey;

    @PostConstruct
    public void init() {
        try {
            byte[] privateBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
            privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(privateSpec);
        } catch (Exception e) {
            log.error("Failed to load private key: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String generateToken(
            UUID userId,
            String email,
            List<UserRole> roles
    ) {
        try {
            JWSSigner signer = new ECDSASigner(privateKey);


            List<String> roleNames = roles.stream()
                    .map(Enum::name)
                    .toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("email", email)
                    .claim("roles", roleNames)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expiration * 1000))
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.ES256),
                    claims
            );

            jwt.sign(signer);
            return jwt.serialize();

        } catch (JOSEException e) {
            log.error("JWT signing failed: {}", e.getMessage());
            throw new InternalServerErrorException("JWT signing failed");
        }
    }
}
