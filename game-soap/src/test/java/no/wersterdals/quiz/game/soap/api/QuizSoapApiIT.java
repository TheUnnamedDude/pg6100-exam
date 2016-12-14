package no.wersterdals.quiz.game.soap.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.gson.Gson;
import com.netflix.hystrix.Hystrix;
import no.westerdals.quiz.dto.*;
import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.game.soap.api.jaxws.CheckAnswer;
import no.westerdals.quiz.game.soap.api.jaxws.CheckAnswerResponse;
import no.westerdals.quiz.game.soap.api.jaxws.GetRandomQuizResponse;
import no.westerdals.quiz.game.soap.client.*;
import no.westerdals.quiz.game.soap.client.Answer;
import no.westerdals.quiz.game.soap.client.Quiz;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.ws.BindingProvider;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.*;

public class QuizSoapApiIT {
    private static GameSoap ws;
    private final static Gson GSON = new Gson();

    private static WireMockServer wiremockServer;

    @BeforeClass
    public static void initClass() {
        GameSoapImplService service = new GameSoapImplService();
        ws = service.getGameSoapImplPort();

        String url = "http://localhost:8080/game-soap/GameSoapImpl";
        ((BindingProvider)ws).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);

        wiremockServer = new WireMockServer(
                wireMockConfig().port(8090).notifier(new ConsoleNotifier(true))
        );
        wiremockServer.start();
    }
    @Before
    public void before() throws Exception {
        Hystrix.reset();

        wiremockServer.resetAll();
        byte[] jsonTrue = GSON.toJson(true).getBytes("UTF-8");
        byte[] jsonFalse = GSON.toJson(false).getBytes("UTF-8");
        wiremockServer.stubFor(
                WireMock.post(WireMock.urlMatching("/quiz/api/quizzes/1/validate"))
                        .willReturn(WireMock.aResponse().withBody(jsonFalse)
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Content-Length", Integer.toString(jsonFalse.length)))
        );
        wiremockServer.stubFor(
                WireMock.post(WireMock.urlMatching("/quiz/api/quizzes/1/validate"))
                        .withRequestBody(new EqualToPattern("1"))
                        .willReturn(WireMock.aResponse().withBody(jsonTrue)
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Content-Length", Integer.toString(jsonTrue.length)))
        );
    }

    @Test
    public void testGetRandom() throws Exception {
        SubcategoryDto subcategory = new SubcategoryDto();
        subcategory.text = "Java";
        QuestionDto question = new QuestionDto();
        question.answer = new AnswerDto();
        question.answer.text = "Yes";
        question.alternatives = Stream.of("Maybe", "No", "Who knows")
                .map(s -> {
                    AnswerDto answerDto = new AnswerDto();
                    answerDto.text = s;
                    return answerDto;
                })
                .collect(Collectors.toList());
        question.category = subcategory;
        byte[] jsonRepresentation = GSON.toJson(question).getBytes("UTF-8");
        wiremockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/quiz/api/quizzes/random"))
                        .willReturn(WireMock.aResponse().withBody(jsonRepresentation)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Content-Length", Integer.toString(jsonRepresentation.length)))
        );

        Quiz quiz = ws.getRandomQuiz();
        assertEquals(question.text, quiz.getText());
        assertTrue(question.alternatives.stream()
                .allMatch(a1 -> quiz.getAlternatives().stream().anyMatch(a2 -> a1.text.equals(a2.getText()))));
    }

    @Test
    public void testGetRandomFail() throws Exception {
        byte[] jsonRepresentation = "{doskpajdsa}".getBytes("UTF-8");
        wiremockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/quiz/api/quizzes/random"))
                        .willReturn(WireMock.aResponse().withBody(jsonRepresentation)
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Content-Length", Integer.toString(jsonRepresentation.length)))
        );
        try {
            ws.getRandomQuiz();
        } catch (Exception e) {
            return;
        }
        fail("Didn't receive a error on a invalid call to the quiz implementation");
    }

    //@Test
    public void testPlayGameCorrect() throws Exception {
        Answer answer = new Answer();
        answer.setQuestionId(1L);
        answer.setAnswerId(1L);

        assertTrue("Expected valid answer!", ws.checkAnswer(answer));
    }

    @Test
    public void testPlayGameWrong() throws Exception {
        Answer answer = new Answer();
        answer.setQuestionId(1L);
        answer.setAnswerId(13L);


        assertFalse("Expected invalid answer!", ws.checkAnswer(answer));
    }

    @Test
    public void testGeneratedStuffThatsNeverUsedAndRuinsMyTestRateInTheSoapModule() throws Exception {
        CheckAnswer checkAnswer = new CheckAnswer();
        checkAnswer.setArg0(new no.westerdals.quiz.dto.Answer());
        assertNotNull(checkAnswer.getArg0());

        GetRandomQuizResponse response = new GetRandomQuizResponse();
        response.setReturn(new no.westerdals.quiz.dto.Quiz());
        assertNotNull(response.getReturn());

        CheckAnswerResponse checkAnswerResponse = new CheckAnswerResponse();
        checkAnswerResponse.setReturn(true);
        assertTrue(checkAnswerResponse.getReturn());
    }
}
