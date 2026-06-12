package com.traintension.gateway.services;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Value("${app.security.jwt.public-key}")
    private String publicKeyBase64;

    private ECPublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        if (publicKeyBase64 == null || publicKeyBase64.isBlank()) {
            throw new IllegalStateException("JWT public key is not configured");
        }
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);
        publicKey = (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(publicSpec);
    }

    public JWTClaimsSet verify(String token) throws Exception {
        log.info("Verifying JWT token in Gateway");
        if (publicKey == null) {
            throw new IllegalStateException("JWT public key is not configured");
        }

        SignedJWT jwt;
        try {
            jwt = SignedJWT.parse(token);
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Malformed JWT token", e);
        }

        if (!JWSAlgorithm.ES256.equals(jwt.getHeader().getAlgorithm())) {
            throw new IllegalArgumentException("Invalid algorithm");
        }

        JWSVerifier verifier = new ECDSAVerifier(publicKey);

        if (!jwt.verify(verifier)) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        JWTClaimsSet claims = jwt.getJWTClaimsSet();

        if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
            throw new IllegalArgumentException("Token expired");
        }

        log.info("JWT token verification successful in Gateway");
        return claims;
    }
}