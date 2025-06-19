package com.ntt.proyecto_trello_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.proyecto_trello_api.dto.request.LoginRequest;
import com.ntt.proyecto_trello_api.dto.request.SignupRequest;
import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.security.JwtUtils;
import com.ntt.proyecto_trello_api.security.UserDetailsImpl;
import com.ntt.proyecto_trello_api.service.RefreshTokenService;
import com.ntt.proyecto_trello_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)

class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserService userService;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User testUser;
    private UserDetailsImpl userDetails;
    
    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        
        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.USER);
        
        userDetails = new UserDetailsImpl(
            1L, "testuser", "test@example.com", "Test", "User", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER")), true
        );
    }
    
    @Test
    void signup_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
    
    @Test
    void signup_ShouldReturnError_WhenUsernameExists() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }
    
    @Test
    void signup_ShouldReturnError_WhenEmailExists() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }
    
    @Test
    void signup_ShouldReturnError_WhenInvalidInput() throws Exception {
        // Given
        signupRequest.setUsername(""); // Invalid empty username
        
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
}
