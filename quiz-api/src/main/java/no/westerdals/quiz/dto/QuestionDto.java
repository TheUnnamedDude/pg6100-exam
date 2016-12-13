package no.westerdals.quiz.dto;

import java.util.List;

public class QuestionDto {
    public Long id;
    public String text;
    // This is for POST's, should not be populated by the converter
    public AnswerDto answer;
    public List<AnswerDto> alternatives;
    public SubcategoryDto category;
}
