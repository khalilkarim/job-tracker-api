package com.jobtracker.api.dto;

import com.jobtracker.api.model.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private String companyName;
    private String jobTitle;
    private String jobDescription;
    private String notes;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
