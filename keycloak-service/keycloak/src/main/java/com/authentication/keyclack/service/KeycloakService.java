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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final KeyCloakConfig config;
    private final RestTemplate restTemplate = new RestTemplate();


    public ResponseEntity<?> login(LoginRequest request) throws JsonProcessingException {
        String tokenUrl = config.getUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";
        log.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA "+tokenUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
            body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());
        body.add("scope","openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
            return ResponseEntity.ok(new ObjectMapper().readTree(response.getBody()));
        } catch (HttpClientErrorException e) {
            log.info("BBBBBBBBBBBBBBBBBBBBBBBBBBB"+e.getMessage()+" "+e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
        log.error("RestClientException occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Keycloak server error");
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
    public ResponseEntity<?> loginAndGetEmail(LoginRequest request) {
        String tokenUrl ="http://localhost:8180/realms/" + config.getRealm() + "/protocol/openid-connect/token";
        log.info("Token URL: " + tokenUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());
        body.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
            JsonNode tokenResponse = new ObjectMapper().readTree(response.getBody());

            String token = tokenResponse.get("access_token").asText();

            // 🔍 Décoder le token et récupérer l'email
            /*SignedJWT jwt = SignedJWT.parse(token);
            String email = (String) jwt.getJWTClaimsSet().getClaim("email");
*/
            return ResponseEntity.ok(Map.of(
                    "email", "hello",
                    "access_token", token
            ));

        } catch (HttpClientErrorException e) {
            log.warn("Keycloak error: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        } catch (Exception e) {
            log.error("Internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "details", e.getMessage()));
        }
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

            // Step 2: Create user in Keycloak
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

            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(userResponse.getStatusCode()).body("Failed to create Keycloak user.");
            }

            // Step 3: Fetch created user from Keycloak to get ID
            String getUsersUrl = userUrl + "?username=" + request.getUsername();
            ResponseEntity<JsonNode> getUsersResponse = restTemplate.exchange(
                    getUsersUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(userHeaders),
                    JsonNode.class
            );

            JsonNode userList = getUsersResponse.getBody();
            if (userList == null || !userList.isArray() || userList.size() == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User created but ID not found.");
            }

            String keycloakId = userList.get(0).get("id").asText();

            // Step 4: Send data to UserProfile MS
            String userProfileUrl = "http://user-profile-ms:8091/api/user/create"; // adjust if needed
            HttpHeaders profileHeaders = new HttpHeaders();
            profileHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> profilePayload = new HashMap<>();
            profilePayload.put("id", keycloakId);
            profilePayload.put("username", request.getUsername());
            profilePayload.put("email", request.getEmail());
            profilePayload.put("firstName", request.getFirstName());
            profilePayload.put("lastName", request.getLastName());

            HttpEntity<Map<String, Object>> profileRequest = new HttpEntity<>(profilePayload, profileHeaders);
            ResponseEntity<String> profileResponse = restTemplate.postForEntity(userProfileUrl, profileRequest, String.class);

            if (!profileResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Keycloak user created, but failed to sync with UserProfile service.");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

}

