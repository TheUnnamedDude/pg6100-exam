package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.SubCategoryDto;
import no.westerdals.quiz.entities.Subcategory;

public class SubcategoryDtoConverter implements DtoConverter<Subcategory, SubCategoryDto> {
    @Override
    public SubCategoryDto convert(Subcategory entity) {
        SubCategoryDto subcategory = new SubCategoryDto();
        subcategory.id = entity.getId();
        subcategory.text = entity.getName();
        subcategory.parentId = entity.getParent().getId();
        return subcategory;
    }
}
