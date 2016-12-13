package no.westerdals.quiz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.SubcategoryDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(CategoryRest.ENDPOINT)
@Api(value = CategoryRest.ENDPOINT, description = "General rest api for quiz categories")
public interface CategoryRest {
    String ENDPOINT = "/categories";

    @ApiOperation("Create a new category")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    Response createCategory(@ApiParam("The contents of the category to be created") CategoryDto categoryDto);

    @ApiOperation("Get all categories")
    @GET
    List<CategoryDto> getCategories(
            @ApiParam("Include subcategories in the entity") @DefaultValue("true") @QueryParam("expand") boolean expand
    );

    @ApiOperation("Get a category by id")
    @GET
    @Path("/{id}")
    CategoryDto getCategory(
            @ApiParam("the id to query for") @PathParam("id") Long id,
            @ApiParam("Add subcategories to this result?") @QueryParam("expand") @DefaultValue("false") boolean expand
    );

    @ApiOperation("Delete a category by id")
    @DELETE
    @Path("/{id}")
    void deleteCategory(@ApiParam("the id for the category to delete") @PathParam("id") Long id);

    @ApiOperation("Update the content of a category")
    @PATCH
    @Path("/{id}")
    @Consumes("application/merge-patch+json")
    Response patchCategory(
            @ApiParam("The id of the category to patch") @PathParam("id") Long id,
            @ApiParam("The entity that should be used for the merge patch") String json
    );

    @ApiOperation("Get all subcategories for the specified category")
    @GET
    @Path("/{parentId}/subcategories")
    Response getSubcategoriesByCategory(@ApiParam("The id of the parent category") @PathParam("parentId") Long parentId);

    @ApiOperation("Create a new subcategory")
    @POST
    @Path("/{parentId}/subcategories")
    Response createSubcategory(
            @ApiParam("Parent category id") @PathParam("parentId") Long parentId,
            @ApiParam("The content of the new category") SubcategoryDto categoryDto
    );

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    List<SubcategoryDto> getSubcategories(
            @ApiParam("a parent category to look for subcategories in") @QueryParam("parentId") Long parentId
    );

    @ApiOperation("Get a subcategory by id")
    @GET
    @Path("/subcategory/{id}")
    SubcategoryDto getSubcategory(@ApiParam("The id of this subcategory") @PathParam("id") Long id);
}
