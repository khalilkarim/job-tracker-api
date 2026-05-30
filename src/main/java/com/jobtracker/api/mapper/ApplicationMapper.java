package com.jobtracker.api.mapper;

import com.jobtracker.api.dto.ApplicationResponse;
import com.jobtracker.api.model.JobApplication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationMapper {

    public ApplicationResponse toResponse(JobApplication application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCompanyName(application.getCompanyName());
        response.setJobTitle(application.getJobTitle());
        response.setJobDescription(application.getJobDescription());
        response.setNotes(application.getNotes());
        response.setAppliedDate(application.getAppliedDate());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        response.setStatus(application.getStatus());

        return response;
    }

    public List<ApplicationResponse> toResponseList(List<JobApplication> applications) {
        return applications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
