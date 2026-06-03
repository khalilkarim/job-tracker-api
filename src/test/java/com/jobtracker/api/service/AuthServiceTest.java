package com.jobtracker.api.service;

import com.jobtracker.api.dto.AuthResponse;
import com.jobtracker.api.dto.LoginRequest;
import com.jobtracker.api.dto.RegisterRequest;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.UserRepository;
import com.jobtracker.api.security.JwtService;
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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    //REGISTER TESTS

    @Test
    public void register_withNewEmail_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Johnny Bravo");
        request.setEmail("johnnyb@gmail.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken("johnnyb@gmail.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("johnnyb@gmail.com", response.getEmail());
        assertEquals("Johnny Bravo", response.getName());

    }

    @Test
    public void register_withExistingEmail_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Johnny Bravo");
        request.setEmail("johnnyb@gmail.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already registered", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    //LOGIN TESTS

    @Test
    public void login_WithValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("johnnyb@gmail.com");
        request.setPassword("secret123");

        User user = new User();
        user.setEmail("johnnyb@gmail.com");
        user.setName("Johnny Bravo");


        when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        when(userRepository.findByEmail("johnnyb@gmail.com"))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken("johnnyb@gmail.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("johnnyb@gmail.com", response.getEmail());
    }

    @Test
    public void login_withInvalidCredentials_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("johnnyb@gmail.com");
        request.setPassword("secret123");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    public void login_withNonExistentEmail_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("johnnyb@gmail.com");
        request.setPassword("secret123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        when(userRepository.findByEmail("johnnyb@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("User not found", exception.getMessage());
    }



}
