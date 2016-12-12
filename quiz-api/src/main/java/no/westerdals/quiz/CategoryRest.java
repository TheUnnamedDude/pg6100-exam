package no.westerdals.quiz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.SubCategoryDto;

import javax.ws.rs.*;
import java.util.List;

@Path(CategoryRest.ENDPOINT)
@Api(value = CategoryRest.ENDPOINT, description = "General rest api for quiz categories")
public interface CategoryRest {
    String ENDPOINT = "/categories";

    @ApiOperation("Create a new category")
    @POST
    void createCategory(CategoryDto categoryDto);

    @ApiOperation("Get all categories")
    @GET
    List<CategoryDto> getCategories(
            @ApiParam("Include subcategories in the entity") @DefaultValue("true") @QueryParam("expand") boolean expand
    );

    @ApiOperation("Get a category by id")
    @GET
    @Path("/{id}")
    CategoryDto getCategory(@ApiParam("the id to query for") @PathParam("id") Long id);

    @ApiOperation("Delete a category by id")
    @DELETE
    @Path("/{id}")
    void deleteCategory(@ApiParam("the id for the category to delete") @PathParam("id") Long id);

    @ApiOperation("Update the content of a category")
    @PATCH
    @Path("/{id}")
    @Consumes("application/merge-patch+json")
    CategoryDto patchCategory(
            @ApiParam("The id of the category to patch") @PathParam("id") Long id,
            @ApiParam("The entity that should be used for the merge patch") String json
    );

    @ApiOperation("Get all subcategories for the specified category")
    @GET
    @Path("/{id}/subcategories")
    List<SubCategoryDto> getSubcategoriesByCategory(@ApiParam("The id of the category") @PathParam("id") Long categoryId);

    @ApiOperation("Create a new subcategory")
    @POST
    @Path("/{id}/subcategories")
    SubCategoryDto createSubcategory(@ApiParam("The content of the new category") CategoryDto categoryDto);

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    List<SubCategoryDto> getSubcategories();

    @ApiOperation("Get a subcategory by id")
    @GET
    @Path("/subcategory/{id}")
    SubCategoryDto getSubcategory(@ApiParam("The id of this subcategory") @PathParam("id") Long id);
}
