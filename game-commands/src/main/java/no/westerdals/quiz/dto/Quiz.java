package no.westerdals.quiz.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Quiz {
    @XmlElement
    private Long id;
    @XmlElement
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
