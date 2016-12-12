package no.westerdals.quiz.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = Question.GET_ALL,
                query = "select q from Question as q"
        ),
        @NamedQuery(
                name = Question.GET_SIZE,
                query = "select count(*) from Question"
        )
})
@Entity
public class Question {
    public static final String GET_ALL = "Question#getAll";
    public static final String GET_SIZE = "Question#getSize";

    @GeneratedValue
    @Id
    private Long id;
    @NotNull
    @Size(min = 4)
    private String text;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Size(min = 3, max = 3)
    private List<Answer> incorrectAnswers;
    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Answer correctAnswer;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setIncorrectAnswers(List<Answer> possibleAnswers) {
        this.incorrectAnswers = possibleAnswers;
    }

    public void setCorrectAnswer(Answer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
