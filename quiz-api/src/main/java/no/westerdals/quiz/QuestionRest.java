package no.westerdals.quiz;

import io.swagger.annotations.Api;
import no.westerdals.quiz.dto.DtoList;
import no.westerdals.quiz.dto.QuestionDto;

import javax.ws.rs.Path;

@Path(QuestionRest.ENDPOINT)
@Api(value = QuestionRest.ENDPOINT, description = "API for general purpose question")
public interface QuestionRest {
    String ENDPOINT = "/quizzes";

    DtoList<QuestionDto> getQuestions();
}
