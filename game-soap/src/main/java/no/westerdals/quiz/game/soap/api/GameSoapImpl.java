package no.westerdals.quiz.game.soap.api;

import no.westerdals.quiz.dto.Answer;
import no.westerdals.quiz.dto.Quiz;
import no.westerdals.quiz.hysterix.QuizRest;

import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless
@WebService(endpointInterface = "no.westerdals.quiz.game.soap.api.GameSoapApi")
public class GameSoapImpl implements GameSoapApi {
    private QuizRest quizRest = new QuizRest("http://localhost:8090/quiz/api");

    @Override
    public Quiz getRandomQuiz() {
        return quizRest.getRandomQuiz();
    }

    @Override
    public Boolean checkAnswer(Answer answerDto) {
        return quizRest.checkAnswer(answerDto.getQuestionId(), answerDto.getAnswerId());
    }
}
