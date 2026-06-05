package com.jobtracker.api.service;

import com.jobtracker.api.dto.ApplicationRequest;
import com.jobtracker.api.dto.AuthResponse;
import com.jobtracker.api.dto.LoginRequest;
import com.jobtracker.api.dto.RegisterRequest;
import com.jobtracker.api.exception.EmailAlreadyExistsException;
import com.jobtracker.api.exception.ResourceNotFoundException;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.UserRepository;
import com.jobtracker.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Johnny Bravo");
        registerRequest.setEmail("johnnyb@gmail.com");
        registerRequest.setPassword("secret123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("johnnyb@gmail.com");
        loginRequest.setPassword("secret123");

        user = new User();
        user.setEmail("johnnyb@gmail.com");
        user.setName("Johnny Bravo");

    }

    //REGISTER TESTS

    @Test
    public void register_withNewEmail_returnsAuthResponse() {
        when(userService.validateEmailNotTaken(registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken("johnnyb@gmail.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("johnnyb@gmail.com", response.getEmail());
        assertEquals("Johnny Bravo", response.getName());

    }

    @Test
    public void register_withExistingEmail_throwsException() {
        when(userService.validateEmailNotTaken(registerRequest.getEmail()))
                .thenThrow(new EmailAlreadyExistsException("Email already registered"));

        RuntimeException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email already registered", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    //LOGIN TESTS

    @Test
    public void login_WithValidCredentials_returnsAuthResponse() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenReturn(user);
        when(jwtService.generateToken("johnnyb@gmail.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("johnnyb@gmail.com", response.getEmail());
    }

    @Test
    public void login_withInvalidCredentials_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    public void login_withNonExistentEmail_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        RuntimeException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }



}
