package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.entities.Category;

import java.util.stream.Collectors;

public class CategoryDtoConverter implements DtoConverter<Category, CategoryDto> {
    private final SubcategoryDtoConverter subcategoryDtoConverter;

    public CategoryDtoConverter(boolean expand) {
        if (expand) {
            this.subcategoryDtoConverter = new SubcategoryDtoConverter();
        } else {
            this.subcategoryDtoConverter = null;
        }
    }
    @Override
    public CategoryDto convert(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.id = category.getId();
        categoryDto.text = category.getText();
        if (subcategoryDtoConverter != null) {
            categoryDto.subcategories = category.getSubcategories()
                    .stream()
                    .map(subcategoryDtoConverter::convert)
                    .collect(Collectors.toList());
        }
        return categoryDto;
    }

}