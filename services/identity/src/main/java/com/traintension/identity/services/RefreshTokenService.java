package com.traintension.identity.services;

import com.github.f4b6a3.uuid.UuidCreator;
import com.traintension.common.exception.custom.SecureException;
import com.traintension.common.exception.custom.UnauthorizedException;
import com.traintension.identity.config.AesEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AesEncryptor aesEncryptor;

    @Value("${app.security.refresh-token.expiration}")
    private long expiration;

    private static final String TOKEN_PREFIX = "refresh:";
    private static final String USER_PREFIX = "refresh:user:";

    public RefreshTokenService(
            @Qualifier("refreshTokenRedisTemplate") RedisTemplate<String, String> redisTemplate,
            AesEncryptor aesEncryptor
    ) {
        this.redisTemplate = redisTemplate;
        this.aesEncryptor = aesEncryptor;
    }

    public String create(UUID userId) {

        String uuid = UuidCreator.getTimeOrderedEpoch().toString();
        String userKey = USER_PREFIX + userId;

        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + uuid,
                userId.toString(),
                expiration,
                TimeUnit.SECONDS
        );

        // Önce bu kullanıcıya ait expire olmuş hayalet üyeleri temizle.
        // Her login/refresh'te çalışarak ZSET'in sürekli temiz kalmasını sağlar.
        redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, Instant.now().getEpochSecond());

        // ZSET score olarak token'ın expire olacağı epoch saniye değerini kullanıyoruz.
        double expiryScore = Instant.now().getEpochSecond() + expiration;
        redisTemplate.opsForZSet().add(userKey, uuid, expiryScore);

        return aesEncryptor.encrypt(uuid);
    }

    public UUID getUserId(String encryptedToken) {

        String uuid;
        try {
            uuid = aesEncryptor.decrypt(encryptedToken);
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + uuid);

        if (userId == null) {
            throw new UnauthorizedException();
        }

        return UUID.fromString(userId);
    }

    public void revoke(String encryptedToken) {

        String uuid;
        try {
            uuid = aesEncryptor.decrypt(encryptedToken);
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + uuid);

        if (userId == null) {
            throw new UnauthorizedException();
        }

        redisTemplate.delete(TOKEN_PREFIX + uuid);
        redisTemplate.opsForZSet().remove(USER_PREFIX + userId, uuid);

    }


    public void revokeAll(UUID userId) {

        String userKey = USER_PREFIX + userId;

        // Önce TTL ile expire olmuş hayalet üyeleri temizle
        redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, Instant.now().getEpochSecond());

        Set<String> uuids = redisTemplate.opsForZSet().range(userKey, 0, -1);

        if (uuids != null && !uuids.isEmpty()) {
            List<String> tokenKeys = uuids.stream()
                    .map(uuid -> TOKEN_PREFIX + uuid)
                    .toList();
            redisTemplate.delete(tokenKeys);
        }

        redisTemplate.delete(userKey);
    }
}