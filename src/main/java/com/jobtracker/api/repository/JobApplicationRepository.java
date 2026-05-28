package com.jobtracker.api.repository;

import com.jobtracker.api.model.ApplicationStatus;
import com.jobtracker.api.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
    int countByUserId(Long userId);
    int countByUserIdAndStatus(Long userId, ApplicationStatus status);

}
