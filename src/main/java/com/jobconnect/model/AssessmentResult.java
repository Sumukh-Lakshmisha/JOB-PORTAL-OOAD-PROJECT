package com.jobconnect.model;

public class AssessmentResult {
    private int correct;
    private int wrong;
    private int notAttempted;

    public AssessmentResult(int correct, int wrong, int notAttempted) {
        this.correct = correct;
        this.wrong = wrong;
        this.notAttempted = notAttempted;
    }

    public int getCorrect() { return correct; }
    public int getWrong() { return wrong; }
    public int getNotAttempted() { return notAttempted; }
}
