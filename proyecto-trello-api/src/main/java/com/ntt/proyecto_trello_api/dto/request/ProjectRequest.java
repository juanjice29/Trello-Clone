package com.ntt.proyecto_trello_api.dto.request;


import com.ntt.proyecto_trello_api.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    private Project.ProjectStatus status = Project.ProjectStatus.ACTIVE;
}
