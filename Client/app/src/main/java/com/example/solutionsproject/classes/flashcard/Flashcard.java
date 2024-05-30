package com.example.solutionsproject.classes.flashcard;

public class Flashcard {
    private final String question;
    private final String answer;
    private final String[] choices;

    public Flashcard(String question, String answer, String[] choices) {
        this.question = question;
        this.answer = answer;
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String[] getChoices() {
        return choices;
    }

    public boolean isCorrect(String answer) {
        return this.answer.equals(answer);
    }
}
