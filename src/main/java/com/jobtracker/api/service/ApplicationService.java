package com.jobtracker.api.service;

import com.jobtracker.api.dto.ApplicationRequest;
import com.jobtracker.api.dto.ApplicationResponse;
import com.jobtracker.api.mapper.ApplicationMapper;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.JobApplicationRepository;
import com.jobtracker.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    JobApplicationRepository jobApplicationRepository;

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    UserRepository userRepository;

    public ApplicationResponse createApplication(String email, ApplicationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setCompanyName(request.getCompanyName());
        application.setJobDescription(request.getJobDescription());
        application.setJobTitle(request.getJobTitle());
        application.setAppliedDate(request.getAppliedDate());
        application.setNotes(request.getNotes());
        application.setStatus(ApplicationStatus.APPLIED);

        JobApplication updated = jobApplicationRepository.save(application);

       return applicationMapper.toResponse(updated);

    }

    public ApplicationResponse getApplication(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return applicationMapper.toResponse(application);
    }

    public List<ApplicationResponse> getApplications(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
       List<JobApplication> applications = jobApplicationRepository.findByUserId(user.getId());

       return applicationMapper.toResponseList(applications);
    }

    public ApplicationResponse updateStatus(String email, Long applicationId, ApplicationStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatus(status);

        JobApplication updated = jobApplicationRepository.save(application);

        return applicationMapper.toResponse(updated);


    }

    public ApplicationResponse updateApplication(
            String email, Long applicationId, ApplicationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        application.setJobDescription(request.getJobDescription());
        application.setJobTitle(request.getJobTitle());
        application.setNotes(request.getNotes());
        application.setCompanyName(request.getCompanyName());
        application.setAppliedDate(request.getAppliedDate());

        JobApplication updated = jobApplicationRepository.save(application);

        return applicationMapper.toResponse(updated);
        }

        public void deleteApplication(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        jobApplicationRepository.delete(application);

        }

    }



