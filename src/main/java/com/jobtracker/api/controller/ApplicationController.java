package com.jobtracker.api.controller;

import com.jobtracker.api.dto.ApplicationRequest;
import com.jobtracker.api.dto.ApplicationResponse;
import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationRequest request
            ) {
        String email = userDetails.getUsername();

        return ResponseEntity.status(201)
                .body( applicationService.createApplication(email, request));

    }

    @GetMapping
    ResponseEntity<List<ApplicationResponse>> getApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(applicationService.getApplications(email));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApplicationResponse> getUserApplication(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(applicationService.getApplication(email, id));

    }

    @PatchMapping("/{id}/status")
    ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestParam ApplicationStatus status) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(applicationService.updateStatus(email, id, status));

    }

    @PutMapping("/{id}")
    ResponseEntity<ApplicationResponse> updateApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestBody ApplicationRequest request) {
        String email = userDetails.getUsername();

        return ResponseEntity.ok(applicationService.updateApplication(email, id, request));


    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        applicationService.deleteApplication(email, id);

        return ResponseEntity.noContent().build();

    }
}
