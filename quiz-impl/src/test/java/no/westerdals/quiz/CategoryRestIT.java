package no.westerdals.quiz;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.dto.SubCategoryDto;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
//import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

    @Test
    public void testCreateCategoryRawHttp() throws Exception {
        String body = "{\"text\":\"økonomi\"}";
        System.out.println("Running raw POST");
        String location = null;
        int responseCode = 0;
        try (Socket socket = new Socket("localhost", 8080)) {
            PrintStream writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
            writer.print("POST /quiz/api/categories HTTP/1.1\n");
            writer.print("Host: localhost:8080\n");
            writer.print("Content-Type: application/json; charset=utf-8\n");
            writer.print("Content-Length: " + body.getBytes("UTF-8").length + "\n\n");
            writer.print(body);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            String line;
            while (!(line = reader.readLine()).trim().isEmpty()) {
                System.out.println(line);
                if (line.startsWith("Location")) {
                    location = line.substring(line.indexOf(":")).trim();
                }
                if (line.startsWith("HTTP")) {
                    String responseInfo = line.substring(line.indexOf(' ') + 1);
                    responseCode = Integer.parseInt(responseInfo.substring(0, responseInfo.indexOf(' ')));
                }
            }
            socket.close();
        }

        assertEquals(201, responseCode);
        assertNotNull(location);
        location = location.replaceAll("https?://", "");

        String[] hostInfo = location.substring(1, location.indexOf('/')).split(":");
        System.out.println("\"" + hostInfo[0] + "\" \"" + hostInfo[1] + "\"");
        try (Socket socket = new Socket(hostInfo[0].trim(), hostInfo.length > 1 ? Integer.parseInt(hostInfo[1]) : 80)) {
            PrintStream writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
            writer.print("GET " + location.substring(location.indexOf('/')) + " HTTP/1.1\n");
            writer.print("Accept: application/json; charset=utf-8\n");
            writer.print("\n");
            socket.shutdownOutput();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            String line;
            while (!(line = reader.readLine()).trim().isEmpty()) {
                System.out.println(line);
                if (line.startsWith("HTTP")) {
                    String responseInfo = line.substring(line.indexOf(' ') + 1);
                    responseCode = Integer.parseInt(responseInfo.substring(0, responseInfo.indexOf(' ')));
                }
            }
            String result = reader.readLine();

            assertTrue("Didn't find the word økonomi in the http response", result.contains("økonomi"));
        }
        assertEquals(200, responseCode);
    }

    @Test
    public void createTwoCategories() throws Exception {
        CategoryDto category1 = new CategoryDto();
        CategoryDto category2 = new CategoryDto();

        category1.text = "Animals";
        category2.text = "E-sport";

        given()
                .contentType(ContentType.JSON)
                .body(category1)
                .post()
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(category2)
                .post()
                .then()
                .statusCode(201);

        CategoryDto[] categories = given()
                .accept(ContentType.JSON)
                .get()
                .then()
                .extract()
                .as(CategoryDto[].class);

        assertTrue("Could not find the first category", Arrays.stream(categories).anyMatch(c -> c.text.equals(category1.text)));
        assertTrue("Could not find the second category", Arrays.stream(categories).anyMatch(c -> c.text.equals(category2.text)));
    }

    @Test
    public void testCreateSubcategory() throws Exception {
        CategoryDto category = new CategoryDto();
        category.text = "Programming";

        String location = given()
                .contentType(ContentType.JSON)
                .body(category)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        SubCategoryDto subCategory = new SubCategoryDto();
        subCategory.text = "Java";

        String subcategoryLocation = given()
                .contentType(ContentType.JSON)
                .body(subCategory)
                .post(location + "/subcategories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        given()
                .accept(ContentType.JSON)
                .get(subcategoryLocation)
                .then()
                .statusCode(200)
                .body("text", is(subCategory.text));
    }

    @Test
    public void testExpand() throws Exception {
        CategoryDto category = new CategoryDto();
        category.text = "Computers";

        String location = given()
                .contentType(ContentType.JSON)
                .body(category)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        SubCategoryDto subCategory = new SubCategoryDto();
        subCategory.text = "Software";

        given()
                .contentType(ContentType.JSON)
                .body(subCategory)
                .post(location + "/subcategories")
                .then()
                .statusCode(201);

        CategoryDto[] categories = given()
                .accept(ContentType.JSON)
                .queryParam("expand", false)
                .get()
                .then()
                .extract()
                .as(CategoryDto[].class);

        Optional<CategoryDto> result = Arrays.stream(categories)
                .filter(c -> c.text.equals(category.text))
                .findAny();
        assertTrue("The result does not contain the category created", result.isPresent());

        assertNull("Expected the result to be null but it was present", result.get().subcategories);

        CategoryDto[] unexpandedCategories = given()
                .accept(ContentType.JSON)
                .queryParam("expand", true)
                .get()
                .then()
                .extract()
                .as(CategoryDto[].class);

        Optional<CategoryDto> unexpandedResult = Arrays.stream(unexpandedCategories)
                .filter(c -> c.text.equals(category.text))
                .findAny();

        assertTrue("The result does not contain the category created", unexpandedResult.isPresent());
        assertNotNull("Expected the result to be null but it was present", unexpandedResult.get().subcategories);

        CategoryDto unexpanded = given()
                .accept(ContentType.JSON)
                .queryParam("expand", false)
                .get(location)
                .then()
                .extract()
                .as(CategoryDto.class);

        assertNull(unexpanded.subcategories);

        CategoryDto expanded = given()
                .accept(ContentType.JSON)
                .queryParam("expand", true)
                .get(location)
                .then()
                .extract()
                .as(CategoryDto.class);

        assertNotNull(expanded.subcategories);
    }

    @Test
    public void testUpdateCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.text = "Unicorns";
        String location = given()
                .contentType(ContentType.JSON)
                .body(categoryDto)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        CategoryDto mergePatchCategory = new CategoryDto();
        mergePatchCategory.text = "Horses";

        String updatedLocation = given()
                .contentType("application/merge-patch+json")
                .body(mergePatchCategory)
                .patch(location)
                .then()
                .statusCode(200)
                .extract()
                .header("Location");

        assertEquals(location, updatedLocation);

        given()
                .accept(ContentType.JSON)
                .get(location)
                .then()
                .statusCode(200)
                .body("text", is(mergePatchCategory.text));
    }

    @Test
    public void testGetSubcategories() throws Exception {
        CategoryDto category1 = new CategoryDto();
        CategoryDto category2 = new CategoryDto();
        category1.text = "Music";
        category2.text = "Browsers";
        String category1Location = given()
                .contentType(ContentType.JSON)
                .body(category1)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        String category2Location = given()
                .contentType(ContentType.JSON)
                .body(category2)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        SubCategoryDto subcategory1 = new SubCategoryDto();
        SubCategoryDto subcategory2 = new SubCategoryDto();
        SubCategoryDto subcategory3 = new SubCategoryDto();

        subcategory1.text = "Rock";
        subcategory2.text = "Chromium";
        subcategory3.text = "Firefox";

        given()
                .contentType(ContentType.JSON)
                .body(subcategory1)
                .post(category1Location + "/subcategories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        given()
                .contentType(ContentType.JSON)
                .body(subcategory2)
                .post(category2Location + "/subcategories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        given()
                .contentType(ContentType.JSON)
                .body(subcategory3)
                .post(category2Location + "/subcategories")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        SubCategoryDto[] subcategories = given()
                .accept(ContentType.JSON)
                .get("subcategories")
                .then()
                .statusCode(200)
                .extract()
                .as(SubCategoryDto[].class);

        assertTrue("Missing subcategory 1", Arrays.stream(subcategories).anyMatch(c -> c.text.equals(subcategory1.text)));
        assertTrue("Missing subcategory 2", Arrays.stream(subcategories).anyMatch(c -> c.text.equals(subcategory2.text)));
        assertTrue("Missing subcategory 3", Arrays.stream(subcategories).anyMatch(c -> c.text.equals(subcategory3.text)));

        CategoryDto category1Result = given()
                .accept(ContentType.JSON)
                .get(category1Location)
                .then()
                .extract()
                .as(CategoryDto.class);
        CategoryDto category2Result = given()
                .accept(ContentType.JSON)
                .get(category2Location)
                .then()
                .extract()
                .as(CategoryDto.class);

        given()
                .accept(ContentType.JSON)
                .queryParam("parentId", category1Result.id)
                .get("subcategories")
                .then()
                .statusCode(200)
                .body("size()", is(1));
        given()
                .accept(ContentType.JSON)
                .get(category2Result.id + "/subcategories")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }
}
