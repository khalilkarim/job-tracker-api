package com.jobtracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiAnalysisResponse {
    private String resumeSuggestions;
    private String interviewQuestions;
}
