package no.westerdals.quiz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;
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
            @ApiParam("Number of results returned") @QueryParam("results") @DefaultValue("20") int results,
            @ApiParam("The ID of the subcategory the result should be in") @QueryParam("filter") Long filter
    );

    @POST
    @ApiOperation("Create a new question")
    Response createQuestion(@ApiParam("The question to create") QuestionDto question);

    @GET
    @ApiOperation("Get a random question")
    @Path("/random")
    QuestionDto getRandomQuestion();

    @GET
    @ApiOperation("Get a question by id")
    @Path("/{id}")
    QuestionDto getQuestion(@ApiParam("The id to look for") @PathParam("id") Long id);

    @ApiOperation("Update the content of a question")
    @PATCH
    @Path("/{id}")
    @Consumes("application/merge-patch+json")
    Response patchQuestion(
            @ApiParam("The id of the question to patch") @PathParam("id") Long id,
            @ApiParam("The entity that should be used for the merge patch") String json
    );
}
