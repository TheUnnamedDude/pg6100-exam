package no.westerdals.quiz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.westerdals.quiz.dto.DtoList;
import no.westerdals.quiz.dto.QuestionDto;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path(QuestionRest.ENDPOINT)
@Api(value = QuestionRest.ENDPOINT, description = "API for general purpose question")
public interface QuestionRest {
    String ENDPOINT = "/quizzes";

    @GET
    @ApiOperation("Get all questions")
    DtoList<QuestionDto> getQuestions(
            @ApiParam("Starting offset for the dataset returned") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam("Number of results returned") @QueryParam("results") @DefaultValue("20") int results
    );

    @POST
    @ApiOperation("Create a new question")
    Response createQuestion(@ApiParam("The question to create") QuestionDto question);

    @GET
    @ApiOperation("Get a question by id")
    @Path("/{id}")
    QuestionDto getQuestion(@ApiParam("The id to look for") @PathParam("id") Long id);

    @GET
    @ApiOperation("Get a random question")
    @Path("/random")
    QuestionDto getRandomQuestion();
}
