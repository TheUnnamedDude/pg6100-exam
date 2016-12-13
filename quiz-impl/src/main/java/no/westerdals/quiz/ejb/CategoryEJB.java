package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Category;
import no.westerdals.quiz.entities.SubCategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

@Stateless
public class CategoryEJB {
    @PersistenceContext
    private EntityManager em;

    public Category createCategory(String name) {
        Category category = new Category();
        category.setText(name);
        em.persist(category);
        return category;
    }

    public Category updateCategory(Long categoryId, String text) {
        Category category = getCategory(categoryId);
        if (text != null) {
            category.setText(text);
        }
        return category;
    }

    public SubCategory createSubCategory(Long parentCategory, String name) {
        Category category = getCategory(parentCategory);
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setParent(category);
        category.getSubcategories().add(subCategory);
        em.persist(subCategory);
        return subCategory;
    }

    public Category getCategory(Long categoryId) {
        return em.find(Category.class, categoryId);
    }

    public SubCategory getSubCategory(Long categoryId) {
        return em.find(SubCategory.class, categoryId);
    }

    public List<Category> getCategories() {
        return em.createNamedQuery(Category.GET_ALL, Category.class).getResultList();
    }

    public Collection<Category> getCategoriesWithSubcategories() {
        List<Category> categories = getCategories();
        categories.stream()
                .map(Category::getSubcategories)
                .flatMap(List::stream)
                .forEach(SubCategory::getName); // Make sure every subcategory is resolved
        return categories;
    }

    public List<SubCategory> getSubCategories() {
        return em.createNamedQuery(SubCategory.GET_ALL, SubCategory.class).getResultList();
    }

    public void deleteCategory(Long categoryId) {
        em.remove(getCategory(categoryId));
    }

    public List<SubCategory> getSubcategoryByParent(Long parentId) {
        return getCategory(parentId).getSubcategories();
    }

    public Category getCategoryWithSubcategories(Long categoryId) {
        Category category = getCategory(categoryId);
        category.getSubcategories().forEach(SubCategory::getName);
        return category;
    }
}
