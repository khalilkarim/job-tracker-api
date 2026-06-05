package com.jobtracker.api.service;

import com.jobtracker.api.dto.AiAnalysisResponse;
import com.jobtracker.api.model.JobApplication;
import com.jobtracker.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private GeminiService geminiService;

    public AiAnalysisResponse analyzeApplication(String email, Long applicationId) {
        User user = userService.findByEmail(email);

       JobApplication application = applicationService.getApplicationEntity(email, applicationId);

        if (application.getJobDescription() == null || application.getJobDescription().isEmpty()) {
            throw new RuntimeException("No job description found for this application");
        }

        String resumePrompt = buildResumePrompt(application);
        String interviewPrompt = buildInterviewPrompt(application);

        String resumeSuggestions = geminiService.analyze(resumePrompt);
        String interviewSuggestions = geminiService.analyze(interviewPrompt);

        return new AiAnalysisResponse(resumeSuggestions, interviewSuggestions);
    }

    private String buildResumePrompt(JobApplication application) {
        return String.format("""
                You are a career coach helping someone tailor their resume 
                specifically to the job they are applying to.
                
                Job Title: %s
                Company: %s
                Job Description: %s
                
                Please provide 5 suggesttions on how to tailor their resume for this role.
                Focus on keywords, skills, and experiences to highlight.
                """,
                application.getJobTitle(),
                application.getCompanyName(),
                application.getJobDescription()
        );
    }

    private String buildInterviewPrompt(JobApplication application) {
        return String.format("""
                You are a career coach helping someone prepare for an interview.
                
                Job Title: %s
                Company: %s
                Job Description: %s
                
                Please provide 5 likely interview questions for this role
                with brief tips on how to answer each one.
                """,
                 application.getJobTitle(),
                 application.getCompanyName(),
                 application.getJobDescription()
        );
    }
}
