package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.entities.Answer;
import no.westerdals.quiz.entities.Question;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionDtoConverter implements DtoConverter<Question, QuestionDto> {
    @Override
    public QuestionDto convert(Question entity) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.text = entity.getText();
        ArrayList<AnswerDto> answers = new ArrayList<>();
        entity.getIncorrectAnswers().stream().map(this::convertAnswer).forEach(answers::add);
        answers.add(convertAnswer(entity.getCorrectAnswer()));
        Collections.shuffle(answers);
        questionDto.alternatives = answers;
        return questionDto;
    }

    private AnswerDto convertAnswer(Answer answer) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.id = answer.getId();
        answerDto.text = answer.getText();
        return answerDto;
    }
}
