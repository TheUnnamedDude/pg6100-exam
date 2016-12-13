package no.westerdals.quiz;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.QuestionDto;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.hamcrest.core.Is.*;

public class CategoryRestIT extends RestITBase {
    @BeforeClass
    public static void initializeClass() {
        waitForJBoss(10);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/categories";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testCreateCategory() throws Exception {
        //QuestionDto questionDto = new QuestionDto();
        //AnswerDto answer = new AnswerDto();
        //answer.text = "Yes";
        //questionDto.text = "Is this working?";
        //questionDto.answer = answer;
        //questionDto.alternatives = Arrays.stream(new String[] {"No", "Maybe", "Who knows"}).map(s -> {
        //    AnswerDto a = new AnswerDto();
        //    a.text = s;
        //    return a;
        //}).collect(Collectors.toList());
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.text = "Integration tests";
        String location =
                given()
                .contentType(ContentType.JSON)
                .body(categoryDto)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        given()
                .accept(ContentType.JSON)
                .get(location)
                .then()
                .statusCode(200)
                .body("text", is(categoryDto.text));
    }


}
