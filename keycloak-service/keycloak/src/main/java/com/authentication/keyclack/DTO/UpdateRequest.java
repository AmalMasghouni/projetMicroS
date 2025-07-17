package com.authentication.keyclack.DTO;

import lombok.Data;

@Data
public class UpdateRequest {
    private String externalId; // Keycloak ID
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
}
