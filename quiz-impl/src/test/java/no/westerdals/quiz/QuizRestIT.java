package no.westerdals.quiz;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.westerdals.quiz.dto.*;
import no.westerdals.quiz.entities.Question;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class QuizRestIT extends RestITBase {

    private final Gson gson = new Gson();

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
        assertTrue(questionDto.alternatives.stream().allMatch(
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

    @Test
    public void testSelfLink() throws Exception {
        createQuestions("HALSelf", "Test", 5);

        DtoList<QuestionDto> questions1 = gson.fromJson(given()
                .accept(ContentType.JSON)
                .queryParam("offset", 1)
                .queryParam("results", 2)
                .get("quizzes")
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());

        DtoList<QuestionDto> questions2 = gson.fromJson(given()
                .accept(ContentType.JSON)
                .get(questions1._links.self.href)
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());

        assertEquals(2, questions1.list.size());
        assertEquals(2, questions2.list.size());
        assertNotNull(questions1._links);
        assertNotNull(questions2._links);

        assertTrue(questions1.list.stream().allMatch(q1 -> questions2.list.stream().anyMatch(q2 -> q1.text.equals(q2.text))));
    }

    @Test
    public void testNextLink() throws Exception {
        ArrayList<QuestionDto> created = createQuestions("HALNext", "Test", 5);
        ArrayList<QuestionDto> found = new ArrayList<>();

        DtoList<QuestionDto> questions = gson.fromJson(given()
                .accept(ContentType.JSON)
                .queryParam("offset", 0)
                .queryParam("results", 3)
                .get("quizzes")
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());
        found.addAll(questions.list);

        while (!questions.list.isEmpty()) {
            questions = gson.fromJson(given()
                    .accept(ContentType.JSON)
                    .get(questions._links.next.href)
                    .then()
                    .statusCode(200)
                    .extract()
                    .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());
            found.addAll(questions.list);
        }

        assertEquals(5, created.size());
        assertTrue("Didn't find all the originally created questions", created.stream().allMatch(q1 -> found.stream().anyMatch(q2 -> q1.text.equals(q2.text))));
    }

    @Test
    public void testPreviousLink() {
        createQuestions("HALPrevious", "Test", 6);
        DtoList<QuestionDto> original = gson.fromJson(given()
                .accept(ContentType.JSON)
                .queryParam("offset", 0)
                .queryParam("results", 3)
                .get("quizzes")
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());


        DtoList<QuestionDto> next = gson.fromJson(given()
                .accept(ContentType.JSON)
                .get(original._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());
        assertEquals(3, next.list.size());
        DtoList<QuestionDto> previous = gson.fromJson(given()
                .accept(ContentType.JSON)
                .get(next._links.previous.href)
                .then()
                .statusCode(200)
                .extract()
                .asString(), new TypeToken<DtoList<QuestionDto>>(){}.getType());
        assertEquals(3, previous.list.size());
        assertEquals(original.list.size(), previous.list.size());
        assertTrue("Results are not the same",
                original.list.stream().allMatch(q1 -> previous.list.stream().anyMatch(q2 -> q1.text.equals(q2.text))));
    }

    private ArrayList<QuestionDto> createQuestions(String category, String subcategoryText, int questionCount) {
        SubcategoryDto subcategory = createSubcategory(category, subcategoryText);
        ArrayList<QuestionDto> questions = new ArrayList<>();

        for (int i = 0; i < questionCount; i++) {
            QuestionDto question = new QuestionDto();
            AnswerDto answer1 = new AnswerDto();
            answer1.text = "Yes";
            question.text = category + subcategoryText + i;
            question.answer = answer1;
            question.category = subcategory; // It should just ignore the extra parameters, so just pass it
            question.alternatives = Arrays.stream(new String[] {"No", "Maybe", "Who knows"}).map(s -> {
                AnswerDto a = new AnswerDto();
                a.text = s;
                return a;
            }).collect(Collectors.toList());

            String location = given()
                    .contentType(ContentType.JSON)
                    .body(question)
                    .post("quizzes")
                    .then()
                    .statusCode(201)
                    .extract()
                    .header("Location");

            questions.add(given()
                    .accept(ContentType.JSON)

                    .get(location)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(QuestionDto.class));
        }

        return questions;
    }
}
