package com.authentication.keyclack.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeyCloakConfig {
    private String url;
    private String realm;
    private String clientId;
    private String clientSecret;
}

