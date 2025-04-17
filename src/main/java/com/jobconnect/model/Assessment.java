package com.jobconnect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Document(collection = "assessments")
public class Assessment {

    @Id
    private String id;
    private String title;
    private List<Question> questions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public static class Question {
        private String question;
        private List<String> options;
        private int correctIndex;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }

        public int getCorrectIndex() { return correctIndex; }
        public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
    }

    // 🧠 Evaluation logic moved here
    public AssessmentResult evaluate(MultiValueMap<String, String> formData) {
        int correct = 0, wrong = 0, notAttempted = 0;

        for (int i = 0; i < questions.size(); i++) {
            String answer = formData.getFirst("q" + i);
            int correctIndex = questions.get(i).getCorrectIndex();

            if (answer == null) {
                notAttempted++;
            } else if (Integer.parseInt(answer) == correctIndex) {
                correct++;
            } else {
                wrong++;
            }
        }

        return new AssessmentResult(correct, wrong, notAttempted);
    }
}
