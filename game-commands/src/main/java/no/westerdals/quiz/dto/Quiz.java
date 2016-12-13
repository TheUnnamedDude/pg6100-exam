package no.westerdals.quiz.dto;

import java.util.List;

public class Quiz {
    private Long id;
    private String text;
    private List<AnswerDto> alternatives;

    public Quiz() {}

    public Quiz(Long id, String text, List<AnswerDto> alternatives) {
        this.id = id;
        this.alternatives = alternatives;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<AnswerDto> getAlternatives() {
        return alternatives;
    }
}
