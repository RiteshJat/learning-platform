package com.evolve.learningplatform.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdatePasswordRequest {
    private String token;
    private String newPassword;
}
