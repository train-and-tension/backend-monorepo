package com.traintension.identity.config;

import com.traintension.identity.config.interfaces.UserProfileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class CoreClientConfig {

    @Value("${core.service.url}")
    private String coreServiceUrl;

    private final CoreServiceConnectionInterceptor connectionInterceptor;

    public CoreClientConfig(CoreServiceConnectionInterceptor connectionInterceptor) {
        this.connectionInterceptor = connectionInterceptor;
    }

    @Bean
    @DependsOn("coreDependencyChecker")
    public RestClient coreRestClient() {
        return RestClient.builder()
                .baseUrl(coreServiceUrl)
                .requestInterceptor(connectionInterceptor)
                .build();
    }

    @Bean
    public UserProfileClient userProfileClient(RestClient coreRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(coreRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(UserProfileClient.class);
    }
}
