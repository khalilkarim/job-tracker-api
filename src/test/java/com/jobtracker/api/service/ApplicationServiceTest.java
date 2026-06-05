package com.jobtracker.api.service;

import com.jobtracker.api.dto.ApplicationRequest;
import com.jobtracker.api.dto.ApplicationResponse;
import com.jobtracker.api.exception.ResourceNotFoundException;
import com.jobtracker.api.exception.UnauthorizedException;
import com.jobtracker.api.mapper.ApplicationMapper;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    @Mock
    JobApplicationRepository jobApplicationRepository;

    @Mock
    UserService userService;

    @Mock
    ApplicationMapper applicationMapper;

    @InjectMocks
    ApplicationService applicationService;

    private User user;
    private JobApplication application;
    private ApplicationRequest request;
    private ApplicationResponse expectedResponse;
    private Long applicationId;
    private  User wrongUser;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1l);
        user.setEmail("johnnyb@gmail.com");
        user.setName("Johnny Bravo");
        user.setPassword("secret123");

        request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setJobDescription("Backend Engineer");
        request.setNotes("notes");
        request.setJobTitle("Software Engineer");
        request.setAppliedDate(LocalDate.of(2024, 1, 15));

        application = new JobApplication();
        application.setUser(user);
        application.setCompanyName(request.getCompanyName());
        application.setStatus(ApplicationStatus.APPLIED);
        application.setJobTitle(request.getJobTitle());
        application.setNotes(request.getNotes());
        application.setJobDescription(request.getJobDescription());
        application.setAppliedDate(request.getAppliedDate());
        application.setCreatedAt(LocalDateTime.now());
        application.setCreatedAt(LocalDateTime.now());

        expectedResponse = new ApplicationResponse();
        expectedResponse.setId(1L);
        expectedResponse.setCompanyName(application.getCompanyName());
        expectedResponse.setCreatedAt(application.getCreatedAt());
        expectedResponse.setAppliedDate(application.getAppliedDate());
        expectedResponse.setJobTitle(application.getJobTitle());
        expectedResponse.setJobDescription(application.getJobDescription());
        expectedResponse.setNotes(application.getNotes());
        expectedResponse.setStatus(application.getStatus());
        expectedResponse.setUpdatedAt(application.getUpdatedAt());

        wrongUser = new User();
        wrongUser.setName("Jim Jones");
        wrongUser.setEmail("jim@gmail.com");
        wrongUser.setId(2L);

        applicationId = 1L;





    }

    @Test
    public void  createApplication_withValidEmail_returnsApplicationResponse() {

        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenReturn(user);
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(applicationMapper.toResponse(any(JobApplication.class)))
                .thenReturn(expectedResponse);

        ApplicationResponse response = applicationService.createApplication(user.getEmail(), request);

        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        assertEquals("Backend Engineer", response.getJobDescription());
        assertEquals("Software Engineer", response.getJobTitle());
        assertEquals("notes", response.getNotes());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
    }

    @Test
    public void createApplication_withInvalidEmail_throwsException() {
        when(userService.findByEmail(user.getEmail()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                applicationService.createApplication(user.getEmail(), request));

        verify(jobApplicationRepository, never()).save(any(JobApplication.class));


    }

    @Test
    public void getApplication_withValidUserAndApplicationId_returnResponse() {
        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenReturn(user);
        when(jobApplicationRepository.findById(1L))
                .thenReturn(Optional.of(application));
        when(applicationMapper.toResponse(any(JobApplication.class)))
                .thenReturn(expectedResponse);

        ApplicationResponse response = applicationService
                .getApplication(user.getEmail(), applicationId);

        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        assertEquals("Backend Engineer", response.getJobDescription());
        assertEquals("Software Engineer", response.getJobTitle());
        assertEquals("notes", response.getNotes());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());





    }

    @Test
    public void getApplication_withInvalidUser_throwsException() {

        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        RuntimeException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getApplication("johnnyb@gmail.com", applicationId)
        );

        assertEquals("User not found", exception.getMessage());

    }

    @Test
    public void getApplication_withUnauthorizedUser_throwsException() {
        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenReturn(wrongUser);
        when(jobApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));


        RuntimeException exception = assertThrows(
                UnauthorizedException.class,
                () -> applicationService.getApplication(user.getEmail(), applicationId)
        );

        assertEquals("Unauthorized", exception.getMessage());


    }

    @Test
    public void deleteApplication_WithValidUser_deleteSuccessfully() {
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(user);
        when(jobApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        applicationService.deleteApplication(user.getEmail(), applicationId);

        verify(jobApplicationRepository).delete(any(JobApplication.class));

    }

    @Test
    public void deleteApplication_withInvalidUser_throwsException() {

        when(userService.findByEmail("johnnyb@gmail.com"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        RuntimeException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.deleteApplication("johnnyb@gmail.com", applicationId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(jobApplicationRepository, never()).delete(any(JobApplication.class));

    }

}
