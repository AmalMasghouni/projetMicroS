package com.programming.techie.userProfileService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.userProfileService.model.UserProfile;
import com.programming.techie.userProfileService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileRepository userProfileRepository ;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Remove "Bearer " prefix from header
            String token = authorizationHeader.replace("Bearer ", "");

            // Decode JWT (no signature verification, just read claims)
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format.");
            }

            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);

            String userId = jsonNode.get("sub").asText(); // "sub" = Keycloak ID (UUID)

            // Fetch profile by Keycloak user ID (stored as externalId)
            UserProfile userProfile = userProfileRepository.findByExternalId(userId);
            if (userProfile != null) {
                return ResponseEntity.ok(userProfile);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the user profile.");
        }
    }



    @PostMapping("/create")
    public  void createUser(@RequestBody UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }


}
