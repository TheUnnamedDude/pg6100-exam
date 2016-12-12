package no.westerdals.quiz.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Answer {

    @GeneratedValue
    @Id
    private Long id;

    @NotNull
    @Size(min = 1)
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    private Question question;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Question getQuestion() {
        return question;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
