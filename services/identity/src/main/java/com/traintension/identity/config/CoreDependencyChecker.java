package com.traintension.identity.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.net.URI;

@Component("coreDependencyChecker")
@Slf4j
public class CoreDependencyChecker {

    @Value("${core.service.url}")
    private String coreServiceUrl;

    @Value("${core.service.startup-timeout:60000}")
    private long startupTimeoutMs;

    @PostConstruct
    public void checkCoreDependency() {
        log.info("Checking if Core service is available at: {}", coreServiceUrl);
        
        try {
            URI uri = new URI(coreServiceUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            
            if (port == -1) {
                port = "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
            }

            long startTime = System.currentTimeMillis();
            boolean isConnected = false;

            while (System.currentTimeMillis() - startTime < startupTimeoutMs) {
                try (Socket socket = new Socket(host, port)) {
                    isConnected = true;
                    log.info("Successfully connected to Core service at {}:{}", host, port);
                    break;
                } catch (Exception e) {
                    log.info("Core service not ready yet at {}:{}. Retrying in 2 seconds...", host, port);
                    Thread.sleep(2000);
                }
            }

            if (!isConnected) {
                String errorMsg = String.format("Failed to connect to Core service at %s:%d after %d ms. Exiting startup.", 
                                                host, port, startupTimeoutMs);
                log.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }

        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new RuntimeException("Error while checking for Core service dependency: " + e.getMessage(), e);
        }
    }
}
