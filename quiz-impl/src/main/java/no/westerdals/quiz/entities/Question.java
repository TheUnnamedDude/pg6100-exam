package no.westerdals.quiz.entities;

import javax.persistence.*;
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
        ),
        @NamedQuery(
                name = Question.GET_ALL_BY_SUBCATEGORY,
                query = "select q from Question as q where q.subcategory.id = :subcategoryId"
        )
})
@Entity
public class Question {
    public static final String GET_ALL = "Question#getAll";
    public static final String GET_SIZE = "Question#getSize";
    public static final String GET_ALL_BY_SUBCATEGORY = "Question#getAllBySubcategory";

    @GeneratedValue
    @Id
    private Long id;

    @NotNull
    @Size(min = 4)
    private String text;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Size(min = 3, max = 3)
    private List<Answer> incorrectAnswers;

    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Answer correctAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    private Subcategory subcategory;


    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIncorrectAnswers(List<Answer> possibleAnswers) {
        this.incorrectAnswers = possibleAnswers;
    }

    public void setCorrectAnswer(Answer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }
}
