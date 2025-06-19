package com.ntt.proyecto_trello_api.controller;

import com.ntt.proyecto_trello_api.dto.request.TaskRequest;
import com.ntt.proyecto_trello_api.dto.response.MessageResponse;
import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.Task;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.security.UserDetailsImpl;
import com.ntt.proyecto_trello_api.service.ProjectService;
import com.ntt.proyecto_trello_api.service.TaskService;
import com.ntt.proyecto_trello_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private boolean hasProjectAccess(Long projectId, User user) {
        return projectService.hasUserAccess(projectId, user);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllTasks(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            List<Task> tasks = taskService.findByProjectOrderByPosition(projectOpt.get());
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long projectId, @PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Task> taskOpt = taskService.findById(id);
        if (taskOpt.isPresent() && taskOpt.get().getProject().getId().equals(projectId)) {
            return ResponseEntity.ok(taskOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest taskRequest) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(taskRequest.getStatus());
            task.setPriority(taskRequest.getPriority());
            task.setPosition(taskRequest.getPosition());
            task.setDueDate(taskRequest.getDueDate());
            task.setProject(projectOpt.get());
            task.setCreator(currentUser);
            
            // Set assignee if provided and valid
            if (taskRequest.getAssigneeId() != null) {
                Optional<User> assigneeOpt = userService.findById(taskRequest.getAssigneeId());
                if (assigneeOpt.isPresent()) {
                    task.setAssignee(assigneeOpt.get());
                }
            }
            
            Task savedTask = taskService.save(task);
            
            // Update labels if provided
            if (taskRequest.getLabelIds() != null && !taskRequest.getLabelIds().isEmpty()) {
                savedTask = taskService.updateTaskLabels(savedTask.getId(), taskRequest.getLabelIds());
            }
            
            return ResponseEntity.ok(savedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long projectId, @PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Task> taskOpt = taskService.findById(id);
        if (taskOpt.isPresent() && taskOpt.get().getProject().getId().equals(projectId)) {
            Task task = taskOpt.get();
            
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(taskRequest.getStatus());
            task.setPriority(taskRequest.getPriority());
            task.setPosition(taskRequest.getPosition());
            task.setDueDate(taskRequest.getDueDate());
            
            // Set assignee if provided and valid
            if (taskRequest.getAssigneeId() != null) {
                Optional<User> assigneeOpt = userService.findById(taskRequest.getAssigneeId());
                if (assigneeOpt.isPresent()) {
                    task.setAssignee(assigneeOpt.get());
                } else {
                    task.setAssignee(null);
                }
            } else {
                task.setAssignee(null);
            }
            
            Task updatedTask = taskService.update(task);
            
            // Update labels if provided
            if (taskRequest.getLabelIds() != null) {
                updatedTask = taskService.updateTaskLabels(updatedTask.getId(), taskRequest.getLabelIds());
            }
            
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long projectId, @PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Task> taskOpt = taskService.findById(id);
        if (taskOpt.isPresent() && taskOpt.get().getProject().getId().equals(projectId)) {
            taskService.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/move")
    public ResponseEntity<?> moveTask(@PathVariable Long projectId, @PathVariable Long id,
                                     @RequestParam Task.TaskStatus status,
                                     @RequestParam Integer position) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        try {
            Task movedTask = taskService.moveTask(id, status, position);
            return ResponseEntity.ok(movedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable Long projectId, @PathVariable Task.TaskStatus status) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            List<Task> tasks = taskService.findByProjectAndStatusOrderByPosition(projectOpt.get(), status);
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchTasks(@PathVariable Long projectId, @RequestParam String q) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            List<Task> tasks = taskService.searchTasks(q, projectOpt.get());
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
