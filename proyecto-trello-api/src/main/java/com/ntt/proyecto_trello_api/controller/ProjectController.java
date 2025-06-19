package com.ntt.proyecto_trello_api.controller;

import com.ntt.proyecto_trello_api.dto.request.ProjectRequest;
import com.ntt.proyecto_trello_api.dto.response.MessageResponse;
import com.ntt.proyecto_trello_api.model.Project;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.security.UserDetailsImpl;
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
@RequestMapping("/api/projects")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        User currentUser = getCurrentUser();
        List<Project> projects = projectService.findByUserInvolvement(currentUser);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Project> projectOpt = projectService.findByIdAndUserInvolvement(id, currentUser);
        
        if (projectOpt.isPresent()) {
            return ResponseEntity.ok(projectOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        User currentUser = getCurrentUser();
        
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStatus(projectRequest.getStatus());
        project.setOwner(currentUser);
        project.getMembers().add(currentUser); // Owner is also a member
        
        Project savedProject = projectService.save(project);
        return ResponseEntity.ok(savedProject);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectRequest) {
        User currentUser = getCurrentUser();
        Optional<Project> projectOpt = projectService.findByIdAndUserInvolvement(id, currentUser);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Only owner can update project details
            if (!project.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Only project owner can update project details"));
            }
            
            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setStatus(projectRequest.getStatus());
            
            Project updatedProject = projectService.update(project);
            return ResponseEntity.ok(updatedProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Project> projectOpt = projectService.findByIdAndUserInvolvement(id, currentUser);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Only owner can delete project
            if (!project.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Only project owner can delete project"));
            }
            
            projectService.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Project deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable Long id, @PathVariable Long userId) {
        User currentUser = getCurrentUser();
        Optional<Project> projectOpt = projectService.findByIdAndUserInvolvement(id, currentUser);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Only owner can add members
            if (!project.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Only project owner can add members"));
            }
            
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                Project updatedProject = projectService.addMember(project, userOpt.get());
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        User currentUser = getCurrentUser();
        Optional<Project> projectOpt = projectService.findByIdAndUserInvolvement(id, currentUser);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Only owner can remove members (except themselves)
            if (!project.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Only project owner can remove members"));
            }
            
            // Cannot remove owner
            if (project.getOwner().getId().equals(userId)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Cannot remove project owner"));
            }
            
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                Project updatedProject = projectService.removeMember(project, userOpt.get());
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam String q) {
        User currentUser = getCurrentUser();
        List<Project> projects = projectService.searchProjects(q, currentUser);
        return ResponseEntity.ok(projects);
    }
}
