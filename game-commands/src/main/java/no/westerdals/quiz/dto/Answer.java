package no.westerdals.quiz.dto;

public class Answer {
    private Long id;
    private String text;

    public Answer() {}

    public Answer(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
