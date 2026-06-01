package com.jobtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private int totalApplicationsCount;
    private int appliedStatusCount;
    private int interviewStatusCount;
    private int offerStatusCount;
    private int rejectedStatusCount;
    private double successRate;

}
