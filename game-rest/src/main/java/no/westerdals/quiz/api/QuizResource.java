package no.westerdals.quiz.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.westerdals.quiz.dto.Answer;
import no.westerdals.quiz.hysterix.QuizRest;
import no.westerdals.quiz.dto.Quiz;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "/api", description = "General rest api for quiz categories")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class QuizResource {
    private QuizRest quizRest;

    public QuizResource(QuizRest quizRest) {
        this.quizRest = quizRest;
    }

    @ApiOperation("Get a random quiz")
    @Path("random")
    @GET
    public Quiz getRandomQuiz() {
        return quizRest.getRandomQuiz();
    }

    @ApiOperation("Check if the answer was correct")
    @Path("games")
    @POST
    public Boolean checkAnswer(@ApiParam("The ID of the answer and question") Answer answerDto) {
        return quizRest.checkAnswer(answerDto.getQuestionId(), answerDto.getAnswerId());
    }
}
