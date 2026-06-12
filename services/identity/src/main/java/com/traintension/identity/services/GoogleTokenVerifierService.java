package com.traintension.identity.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(Environment env) {
        List<String> clientIds = new ArrayList<>();
        int i = 0;
        String value;
        while ((value = env.getProperty("google.client-ids[" + i + "]")) != null) {
            clientIds.add(value);
            i++;
        }
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(clientIds)
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) throw new BadRequestException();
            return idToken.getPayload();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}
