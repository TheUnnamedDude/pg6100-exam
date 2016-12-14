package no.westerdals.quiz.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Answer {
    @XmlElement
    private Long answerId;
    @XmlElement
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
