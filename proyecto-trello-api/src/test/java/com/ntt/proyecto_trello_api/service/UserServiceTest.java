package com.ntt.proyecto_trello_api.service;


import com.ntt.proyecto_trello_api.model.User;
import com.ntt.proyecto_trello_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);
        testUser.setEnabled(true);
    }
    
    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userService.findById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userService.findById(1L);
        
        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userService.findByUsername("testuser");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    void existsByUsername_ShouldReturnTrue_WhenUserExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When
        boolean result = userService.existsByUsername("testuser");
        
        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }
    
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When
        boolean result = userService.existsByEmail("test@example.com");
        
        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }
    
    @Test
    void save_ShouldEncodePasswordAndSaveUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plainpassword");
        
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // When
        User result = userService.save(newUser);
        
        // Then
        verify(passwordEncoder).encode("plainpassword");
        verify(userRepository).save(newUser);
        assertNotNull(result);
    }
    
    @Test
    void findActiveUsers_ShouldReturnEnabledUsers() {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByEnabledTrue()).thenReturn(activeUsers);
        
        // When
        List<User> result = userService.findActiveUsers();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository).findByEnabledTrue();
    }
    
    @Test
    void searchUsers_ShouldReturnMatchingUsers() {
        // Given
        List<User> matchingUsers = Arrays.asList(testUser);
        when(userRepository.findBySearchTerm("test")).thenReturn(matchingUsers);
        
        // When
        List<User> result = userService.searchUsers("test");
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository).findBySearchTerm("test");
    }
    
    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // When
        userService.deleteById(1L);
        
        // Then
        verify(userRepository).deleteById(1L);
    }
}
