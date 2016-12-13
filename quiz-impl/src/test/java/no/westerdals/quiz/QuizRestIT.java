package no.westerdals.quiz;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.dto.SubcategoryDto;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class QuizRestIT extends RestITBase {

    @BeforeClass
    public static void initializeClass() {
        waitForJBoss(10);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private SubcategoryDto createSubcategory(String category, String subcategory) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.text = category;

        SubcategoryDto subcategoryDto = new SubcategoryDto();
        subcategoryDto.text = subcategory;

        String categoryLocation = given()
                .contentType(ContentType.JSON)
                .body(categoryDto)
                .post("categories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        String subcategoryLocation = given()
                .contentType(ContentType.JSON)
                .body(subcategoryDto)
                .post(categoryLocation + "/subcategories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        return given()
                .accept(ContentType.JSON)
                .get(subcategoryLocation)
                .then()
                .statusCode(200)
                .extract()
                .as(SubcategoryDto.class);
    }

    @Test
    public void testCreateAndGetQuiz() throws Exception {

        SubcategoryDto resultCategory = createSubcategory("Exam", "Enterprise java");

        QuestionDto questionDto = new QuestionDto();
        AnswerDto answer = new AnswerDto();
        answer.text = "Yes";
        questionDto.text = "Is this working?";
        questionDto.answer = answer;
        questionDto.category = resultCategory; // It should just ignore the extra parameters, so just pass it
        questionDto.alternatives = Arrays.stream(new String[] {"No", "Maybe", "Who knows"}).map(s -> {
            AnswerDto a = new AnswerDto();
            a.text = s;
            return a;
        }).collect(Collectors.toList());

        String questionLocation = given()
                .contentType(ContentType.JSON)
                .body(questionDto)
                .post("quizzes")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        QuestionDto questionResult = given()
                .accept(ContentType.JSON)
                .get(questionLocation)
                .then()
                .statusCode(200)
                .extract()
                .as(QuestionDto.class);

        assertEquals(questionDto.text, questionResult.text);
        assertNotNull(questionDto.category);
        assertEquals(resultCategory.id, questionResult.category.id);
        assertTrue(questionDto.alternatives.stream().anyMatch(
                q1 -> questionResult.alternatives.stream().anyMatch(q2 -> q1.text.equals(q2.text))));
    }

    @Test
    public void testGetRandom() {
        SubcategoryDto resultCategory = createSubcategory("This", "RESTful API");

        QuestionDto question1 = new QuestionDto();
        AnswerDto answer1 = new AnswerDto();
        answer1.text = "Yes";
        question1.text = "Does getting questions by random work?";
        question1.answer = answer1;
        question1.category = resultCategory; // It should just ignore the extra parameters, so just pass it
        question1.alternatives = Arrays.stream(new String[] {"No", "Maybe", "Who knows"}).map(s -> {
            AnswerDto a = new AnswerDto();
            a.text = s;
            return a;
        }).collect(Collectors.toList());

        QuestionDto question2 = new QuestionDto();
        AnswerDto answer2 = new AnswerDto();
        answer2.text = "Maybe";
        question2.text = "Does getting questions by random actually work though?";
        question2.answer = answer2;
        question2.category = resultCategory; // It should just ignore the extra parameters, so just pass it
        question2.alternatives = Arrays.stream(new String[] {"No", "Yes", "Who knows"}).map(s -> {
            AnswerDto a = new AnswerDto();
            a.text = s;
            return a;
        }).collect(Collectors.toList());

        given()
                .contentType(ContentType.JSON)
                .body(question1)
                .post("quizzes")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(question2)
                .post("quizzes")
                .then()
                .statusCode(201);

        boolean foundSame = false;
        boolean foundDifferent = false;
        for (int i = 0; i < 400; i++) { // 400 samples should be more then enough, there is a really small chance this test fails
            QuestionDto randomQuestion = given()
                    .accept(ContentType.JSON)
                    .get("quizzes/random")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(QuestionDto.class);
            if (randomQuestion.text.equals(question1.text))
                foundSame = true;
            else
                foundDifferent = true;
            if (foundSame && foundDifferent)
                break;
        }

        assertTrue("Didn't find the same question", foundSame);
        assertTrue("Didn't find a different question", foundDifferent);
    }
}
