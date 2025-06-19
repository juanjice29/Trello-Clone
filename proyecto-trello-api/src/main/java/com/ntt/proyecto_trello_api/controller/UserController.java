package com.ntt.proyecto_trello_api.controller;

import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.security.UserDetailsImpl;
import com.ntt.proyecto_trello_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String q) {
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
}
