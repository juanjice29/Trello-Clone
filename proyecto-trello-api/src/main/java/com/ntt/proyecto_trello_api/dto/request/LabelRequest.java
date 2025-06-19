package com.ntt.proyecto_trello_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelRequest {
    
    @NotBlank
    @Size(max = 50)
    private String name;
    
    @Size(max = 7)
    private String color = "#808080";
}
