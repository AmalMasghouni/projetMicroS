package com.programming.techie.userProfileService.controller;

import com.programming.techie.userProfileService.model.UserProfile;
import com.programming.techie.userProfileService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    UserProfileRepository userProfileRepository ;


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable String id) {
        try {
            UserProfile userProfile = userProfileRepository.findById(id).orElse(null);
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
