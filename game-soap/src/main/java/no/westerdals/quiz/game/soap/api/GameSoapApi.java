package no.westerdals.quiz.game.soap.api;

import no.westerdals.quiz.dto.Answer;
import no.westerdals.quiz.dto.Quiz;

import javax.jws.WebService;

@WebService(name = "GameSoap")
public interface GameSoapApi {
    Quiz getRandomQuiz();
    Boolean checkAnswer(Answer answerDto);
}
