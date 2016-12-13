package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.SubcategoryDto;
import no.westerdals.quiz.entities.Subcategory;

public class SubcategoryDtoConverter implements DtoConverter<Subcategory, SubcategoryDto> {
    @Override
    public SubcategoryDto convert(Subcategory entity) {
        SubcategoryDto subcategory = new SubcategoryDto();
        subcategory.id = entity.getId();
        subcategory.text = entity.getName();
        subcategory.parentId = entity.getParent().getId();
        return subcategory;
    }
}
