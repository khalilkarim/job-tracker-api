package com.jobtracker.api.service;

import com.jobtracker.api.dto.DashboardResponse;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.JobApplicationRepository;
import com.jobtracker.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JobApplicationRepository jobApplicationRepository;


    public DashboardResponse getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int totalApplications = jobApplicationRepository.countByUserId(user.getId());
        int appliedStatusTotal = jobApplicationRepository.countByUserIdAndStatus(
                user.getId(), ApplicationStatus.APPLIED);
        int interviewStatusTotal = jobApplicationRepository.countByUserIdAndStatus(
                user.getId(), ApplicationStatus.INTERVIEW);
        int offerStatusTotal = jobApplicationRepository.countByUserIdAndStatus(
                user.getId(), ApplicationStatus.OFFER);
        int rejectedStatusTotal = jobApplicationRepository.countByUserIdAndStatus(
                user.getId(), ApplicationStatus.REJECTED);
        double successRate = totalApplications > 0
                ? Math.round(((double) offerStatusTotal / totalApplications) * 100.0) : 0.0;

        return new DashboardResponse(
                totalApplications,
                appliedStatusTotal,
                interviewStatusTotal,
                offerStatusTotal,
                rejectedStatusTotal,
                successRate
        );


    }

}
