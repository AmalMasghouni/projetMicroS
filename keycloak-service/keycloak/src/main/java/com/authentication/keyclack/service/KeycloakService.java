package com.authentication.keyclack.service;

import com.authentication.keyclack.DTO.LoginRequest;
import com.authentication.keyclack.DTO.LogoutRequest;
import com.authentication.keyclack.DTO.RegisterRequest;
import com.authentication.keyclack.configuration.KeyCloakConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeyCloakConfig config;
    private final RestTemplate restTemplate = new RestTemplate();


    public ResponseEntity<?> login(LoginRequest request) throws JsonProcessingException {
        if (!userExists(request.getUsername())) {
            return ResponseEntity.status(404).body("{\"error\": \"User not found\"}");
        }
        String tokenUrl = config.getUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
            return ResponseEntity.ok(new ObjectMapper().readTree(response.getBody()));
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private String getAdminAccessToken() throws JsonProcessingException {
        String tokenUrl = config.getUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
        JsonNode tokenResponse = new ObjectMapper().readTree(response.getBody());

        return tokenResponse.get("access_token").asText();
    }
    private boolean userExists(String username) throws JsonProcessingException {
        // First get admin token (client credentials)
        String adminToken = getAdminAccessToken();

        String url = config.getUrl() + "/admin/realms/" + config.getRealm() + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode users = new ObjectMapper().readTree(response.getBody());

            return users.isArray() && users.size() > 0;
        } catch (Exception e) {
            // handle or log error, here return false meaning user doesn't exist or error
            e.printStackTrace();
            return false;
        }
    }
    public ResponseEntity<?> logout(LogoutRequest request) {
        String logoutUrl = config.getUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("refresh_token", request.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(logoutUrl, entity, String.class);
            return ResponseEntity.ok(Map.of("message", "Logged out"));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        String tokenUrl = config.getUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";
        String userUrl = config.getUrl() + "/admin/realms/" + config.getRealm() + "/users";

        // Step 1: Get admin token using client_credentials
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
        tokenBody.add("grant_type", "client_credentials");
        tokenBody.add("client_id", config.getClientId());
        tokenBody.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);

        try {
            ResponseEntity<JsonNode> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, JsonNode.class);
            String token = tokenResponse.getBody().get("access_token").asText();

            // Step 2: Create user
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setContentType(MediaType.APPLICATION_JSON);
            userHeaders.setBearerAuth(token);

            Map<String, Object> user = new HashMap<>();
            user.put("username", request.getUsername());
            user.put("email", request.getEmail());
            user.put("enabled", true);
            user.put("firstName", request.getFirstName());
            user.put("lastName", request.getLastName());
            user.put("credentials", List.of(Map.of(
                    "type", "password",
                    "value", request.getPassword(),
                    "temporary", false
            )));

            HttpEntity<Map<String, Object>> userRequest = new HttpEntity<>(user, userHeaders);

            ResponseEntity<String> userResponse = restTemplate.postForEntity(userUrl, userRequest, String.class);
            return ResponseEntity.status(userResponse.getStatusCode()).body("User created");

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}

