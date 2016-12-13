package no.westerdals.quiz.api;

import no.westerdals.quiz.hysterix.QuizRest;
import no.westerdals.quiz.dto.Quiz;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/game/api")
@Produces(MediaType.APPLICATION_JSON)
public class QuizResource {
    private QuizRest quizRest;

    public QuizResource(QuizRest quizRest) {
        this.quizRest = quizRest;
    }

    @Path("random")
    @GET
    public Quiz getRandomQuiz() {
        return quizRest.getRandomQuiz();
    }
}
