package com.authentication.keyclack.controller;

import com.authentication.keyclack.DTO.LoginRequest;
import com.authentication.keyclack.DTO.LogoutRequest;
import com.authentication.keyclack.DTO.RegisterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.authentication.keyclack.service.KeycloakService;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws JsonProcessingException {
        try {
            return keycloakService.login(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/user-email")
    public ResponseEntity<?> getUserEmail(@RequestBody LoginRequest request) {
       /* String email = jwt.getClaimAsString("email");

        return ResponseEntity.ok(Map.of("email", email));*/
        try {
            return keycloakService.loginAndGetEmail(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /*@GetMapping("/userEmailll")
    public ResponseEntity<?> getUserEmaillll(Authentication authentication) {
      Jwt jwt=(Jwt) authentication.getPrincipal();
      String email=jwt.getClaim("email");
      return ResponseEntity.ok(email);

    }*/



    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody LoginRequest request) throws JsonProcessingException {
        return ResponseEntity.ok("test") ;
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        return keycloakService.logout(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return keycloakService.register(request);
    }
}

