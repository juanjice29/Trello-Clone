package com.ntt.proyecto_trello_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    
    @NotBlank
    private String refreshToken;
}
