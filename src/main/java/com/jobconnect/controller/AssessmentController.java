package com.jobconnect.controller;

import com.jobconnect.model.Assessment;
import com.jobconnect.model.AssessmentResult;
import com.jobconnect.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.MultiValueMap;

@Controller
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    // Show form to create a new assessment
    @GetMapping("/createAssessment")
    public String showCreateAssessmentForm() {
        return "assessment/createAssessment";  // View for creating a new assessment
    }

    // Create a new assessment through a POST request
    @PostMapping("/createassessment")
    @ResponseBody
    public ResponseEntity<?> createAssessment(@RequestBody Assessment assessment) {
        try {
            Assessment createdAssessment = assessmentService.createAssessment(assessment);
            return ResponseEntity.ok(createdAssessment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating assessment: " + e.getMessage());
        }
    }

    // Render the assessment form by title (get the assessment from the database)
    @GetMapping("/takeAssessment")
    public String takeAssessment(@RequestParam String title, Model model) {
        Assessment assessment = assessmentService.getAssessmentByTitle(title);
        if (assessment == null) {
            return "error"; // Return error if assessment doesn't exist
        }
        model.addAttribute("assessment", assessment);
        return "assessment/takeAssessment";  // The form where users answer questions
    }

    // Handle form submission and calculate the result
    @PostMapping("/submitAssessment")
    public String submitAssessment(@RequestParam String title,
                                   @RequestParam MultiValueMap<String, String> formData,
                                   Model model) {
        Assessment assessment = assessmentService.getAssessmentByTitle(title);
        if (assessment == null) {
            return "error";
        }

        // Calculate the result of the assessment
        AssessmentResult result = assessment.evaluate(formData);

        // Pass the result to the view
        model.addAttribute("correct", result.getCorrect());
        model.addAttribute("wrong", result.getWrong());
        model.addAttribute("notAttempted", result.getNotAttempted());
        return "assessment/result";  // The page displaying the test results
    }
}
