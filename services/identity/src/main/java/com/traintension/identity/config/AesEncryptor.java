package com.traintension.identity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptor {
    @Value("${app.security.encryption.key}")
    private String key;

    @Value("${app.security.encryption.salt}")
    private String salt;

    public String encrypt(String text) {
        return Encryptors.text(key, salt).encrypt(text);
    }

    public String decrypt(String text) {
        return Encryptors.text(key, salt).decrypt(text);
    }
}
