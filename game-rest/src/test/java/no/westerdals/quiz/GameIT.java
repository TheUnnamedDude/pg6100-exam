package no.westerdals.quiz;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.google.gson.Gson;
import com.netflix.hystrix.Hystrix;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.westerdals.quiz.dto.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class GameIT {

    private static final Gson GSON = new Gson();

    @ClassRule
    public static final DropwizardAppRule<QuizConfiguration> RULE = new DropwizardAppRule<>(QuizApplication.class);

    private static WireMockServer wiremockServer;

    @BeforeClass
    public static void initClass() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = RULE.getLocalPort();
        RestAssured.basePath = "/game/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

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

        Quiz quiz = given()
                .accept(ContentType.JSON)
                .get("random")
                .then()
                .statusCode(200)
                .extract()
                .as(Quiz.class);
        assertNotNull(quiz);
        assertEquals(question.text, quiz.getText());
        assertTrue(question.alternatives.stream()
                .allMatch(a1 -> quiz.getAlternatives().stream().anyMatch(a2 -> a1.text.equals(a2.text))));
    }

    @Test
    public void testGetRandomFail() throws Exception {
        byte[] jsonRepresentation = "{doskpajdsa}".getBytes("UTF-8"); // Send hystrix invalid json
        wiremockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/quiz/api/quizzes/random"))
                        .willReturn(WireMock.aResponse().withBody(jsonRepresentation)
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Content-Length", Integer.toString(jsonRepresentation.length)))
        );

        given()
                .accept(ContentType.JSON)
                .get("random")
                .then()
                .statusCode(500); // We expect hystrix to give a 500 whenever it gets a invalid response
    }

    @Test
    public void testPlayGameCorrect() throws Exception {
        Boolean result = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(new Answer(1L, 1L))
                .post("games")
                .then()
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertTrue("Expected valid answer!", result);
    }

    @Test
    public void testPlayGameWrong() throws Exception {
        Boolean result = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(new Answer(13L, 1L))
                .post("games")
                .then()
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertFalse("Expected invalid answer!", result);
    }
}
