package no.westerdals.quiz.dto;

public class Answer {
    private Long answerId;
    private Long questionId;

    public Answer() {}

    public Answer(Long answerId, Long questionId) {
        this.answerId = answerId;
        this.questionId = questionId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
