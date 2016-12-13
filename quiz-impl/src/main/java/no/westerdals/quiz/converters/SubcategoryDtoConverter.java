package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.SubCategoryDto;
import no.westerdals.quiz.entities.SubCategory;

public class SubcategoryDtoConverter implements DtoConverter<SubCategory, SubCategoryDto> {
    @Override
    public SubCategoryDto convert(SubCategory entity) {
        SubCategoryDto subcategory = new SubCategoryDto();
        subcategory.id = entity.getId();
        subcategory.text = entity.getName();
        subcategory.parentId = entity.getParent().getId();
        return subcategory;
    }
}
