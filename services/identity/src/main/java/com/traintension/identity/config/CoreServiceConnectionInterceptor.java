package com.traintension.identity.config;

import com.traintension.common.exception.custom.SecureException;
import com.traintension.common.exception.custom.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

@Component
@Slf4j
public class CoreServiceConnectionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            return execution.execute(request, body);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(
                    "Connection failed to Core Service at URI: {}",
                    request.getURI().toString()
            );
        }
    }
}
