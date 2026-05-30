package com.jobtracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String jobDescription;
    private String notes;

    @NotNull(message = "Applied date is required")
    private LocalDate appliedDate;
}
