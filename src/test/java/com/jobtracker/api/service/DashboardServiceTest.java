package com.jobtracker.api.service;

import com.jobtracker.api.dto.DashboardResponse;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.JobApplicationRepository;
import com.jobtracker.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    public void getDashboard_withValidUser_returnsDashboardResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("johnnyb@gmail.com");

        when(userRepository.findByEmail("johnnyb@gmail.com"))
                .thenReturn(Optional.of(user));
        when(applicationRepository.countByUserId(1L))
                .thenReturn(10);
        when(applicationRepository.countByUserIdAndStatus(1L, ApplicationStatus.APPLIED))
                .thenReturn(5);
        when(applicationRepository.countByUserIdAndStatus(1L, ApplicationStatus.INTERVIEW))
                .thenReturn(3);
        when(applicationRepository.countByUserIdAndStatus(1L, ApplicationStatus.OFFER))
                .thenReturn(1);
        when(applicationRepository.countByUserIdAndStatus(1L, ApplicationStatus.REJECTED))
                .thenReturn(1);

        DashboardResponse response = dashboardService.getDashboard("johnnyb@gmail.com");

        assertNotNull(response);
        assertEquals(10, response.getTotalApplicationsCount());
        assertEquals(5, response.getAppliedStatusCount());
        assertEquals(3, response.getInterviewStatusCount());
        assertEquals(1, response.getOfferStatusCount());
        assertEquals(1, response.getRejectedStatusCount());
        assertEquals(10.0, response.getSuccessRate());
    }

    @Test
    public void getDashboard_withNoApplications_returnsZeroSuccessRate() {
        User user = new User();
        user.setId(1L);
        user.setEmail("johnnyb@gmail.com");

        when(userRepository.findByEmail("johnnyb@gmail.com"))
                .thenReturn(Optional.of(user));
        when(applicationRepository.countByUserId(1L))
                .thenReturn(0);
        when(applicationRepository.countByUserIdAndStatus(any(), any()))
                .thenReturn(0);

        DashboardResponse response = dashboardService.getDashboard("johnnyb@gmail.com");

        assertNotNull(response);
        assertEquals(0, response.getTotalApplicationsCount());
        assertEquals(0.0, response.getSuccessRate());
    }

    @Test
    public void getDashboard_withInvalidUser_throwsException() {
        when(userRepository.findByEmail("johnnyb@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> dashboardService.getDashboard("johnnyb@gmail.com")
        );

        assertEquals("User not found", exception.getMessage());
    }
}
