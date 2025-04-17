package com.jobconnect.service;

import com.jobconnect.model.Assessment;
import com.jobconnect.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    public Assessment createAssessment(Assessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public Assessment getAssessmentByTitle(String title) {
        return assessmentRepository.findByTitle(title).orElse(null);
    }
    
}
