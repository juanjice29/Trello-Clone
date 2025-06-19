package com.ntt.proyecto_trello_api.dto.request;


import com.ntt.proyecto_trello_api.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskRequest {
    
    @NotBlank
    @Size(max = 100)
    private String title;
    
    @Size(max = 1000)
    private String description;
    
    private Task.TaskStatus status = Task.TaskStatus.TODO;
    
    private Task.Priority priority = Task.Priority.MEDIUM;
    
    private Integer position = 0;
    
    private LocalDateTime dueDate;
    
    private Long assigneeId;
    
    private Set<Long> labelIds;
}
