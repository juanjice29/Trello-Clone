package com.ntt.proyecto_trello_api.controller;

import com.ntt.proyecto_trello_api.dto.request.LabelRequest;
import com.ntt.proyecto_trello_api.dto.response.MessageResponse;
import com.ntt.proyecto_trello_api.model.Label;
import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.security.UserDetailsImpl;
import com.ntt.proyecto_trello_api.service.LabelService;
import com.ntt.proyecto_trello_api.service.ProjectService;
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
@RequestMapping("/api/projects/{projectId}/labels")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class LabelController {
    
    @Autowired
    private LabelService labelService;
    
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
    public ResponseEntity<?> getAllLabels(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            List<Label> labels = labelService.findByProject(projectOpt.get());
            return ResponseEntity.ok(labels);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getLabelById(@PathVariable Long projectId, @PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            Optional<Label> labelOpt = labelService.findByIdAndProject(id, projectOpt.get());
            if (labelOpt.isPresent()) {
                return ResponseEntity.ok(labelOpt.get());
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<?> createLabel(@PathVariable Long projectId, @Valid @RequestBody LabelRequest labelRequest) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Check if label name already exists in project
            if (labelService.existsByNameAndProject(labelRequest.getName(), project)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Label name already exists in this project"));
            }
            
            Label label = new Label();
            label.setName(labelRequest.getName());
            label.setColor(labelRequest.getColor());
            label.setProject(project);
            
            Label savedLabel = labelService.save(label);
            return ResponseEntity.ok(savedLabel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLabel(@PathVariable Long projectId, @PathVariable Long id, @Valid @RequestBody LabelRequest labelRequest) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            Optional<Label> labelOpt = labelService.findByIdAndProject(id, project);
            
            if (labelOpt.isPresent()) {
                Label label = labelOpt.get();
                
                // Check if new name conflicts with existing labels (excluding current one)
                Optional<Label> existingLabel = labelService.findByNameAndProject(labelRequest.getName(), project);
                if (existingLabel.isPresent() && !existingLabel.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Label name already exists in this project"));
                }
                
                label.setName(labelRequest.getName());
                label.setColor(labelRequest.getColor());
                
                Label updatedLabel = labelService.update(label);
                return ResponseEntity.ok(updatedLabel);
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long projectId, @PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        if (!hasProjectAccess(projectId, currentUser)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Access denied to this project"));
        }
        
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            Optional<Label> labelOpt = labelService.findByIdAndProject(id, project);
            
            if (labelOpt.isPresent()) {
                labelService.deleteById(id);
                return ResponseEntity.ok(new MessageResponse("Label deleted successfully"));
            }
        }
        return ResponseEntity.notFound().build();
    }
}
