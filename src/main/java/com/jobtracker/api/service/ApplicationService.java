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
import com.jobtracker.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public ApplicationResponse createApplication(String email, ApplicationRequest request) {
        User user = userService.findByEmail(email);

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
        User user = userService.findByEmail(email);
        JobApplication application = findAndValidateOwnership(user, applicationId);

        return applicationMapper.toResponse(application);
    }

    public List<ApplicationResponse> getApplications(String email) {
        User user = userService.findByEmail(email);
       List<JobApplication> applications = jobApplicationRepository.findByUserId(user.getId());

       return applicationMapper.toResponseList(applications);
    }

    public ApplicationResponse updateStatus(String email, Long applicationId, ApplicationStatus status) {
        User user = userService.findByEmail(email);

        JobApplication application = findAndValidateOwnership(user, applicationId);

        application.setStatus(status);

        JobApplication updated = jobApplicationRepository.save(application);

        return applicationMapper.toResponse(updated);


    }

    public ApplicationResponse updateApplication(
            String email, Long applicationId, ApplicationRequest request) {
        User user = userService.findByEmail(email);
        JobApplication application = findAndValidateOwnership(user, applicationId);
        application.setJobDescription(request.getJobDescription());
        application.setJobTitle(request.getJobTitle());
        application.setNotes(request.getNotes());
        application.setCompanyName(request.getCompanyName());
        application.setAppliedDate(request.getAppliedDate());

        JobApplication updated = jobApplicationRepository.save(application);

        return applicationMapper.toResponse(updated);
        }

        public void deleteApplication(String email, Long applicationId) {
        User user = userService.findByEmail(email);
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        jobApplicationRepository.delete(application);

        }

        public JobApplication getApplicationEntity(String email, Long applicationId) {
        User user = userService.findByEmail(email);
        return findAndValidateOwnership(user, applicationId);

    }

        private JobApplication findAndValidateOwnership(User user, Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
         if (!application.getUser().getId().equals(user.getId())) {
             throw new UnauthorizedException("Unauthorized");
         }
         return application;

        }

    }



