package com.evolve.learningplatform.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String role; // e.g. ROLE_USER
}