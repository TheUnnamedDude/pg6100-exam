package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Category;
import no.westerdals.quiz.entities.Subcategory;

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

    public Subcategory createSubcategory(Long parentCategory, String name) {
        Category category = getCategory(parentCategory);
        Subcategory subcategory = new Subcategory();
        subcategory.setName(name);
        subcategory.setParent(category);
        category.getSubcategories().add(subcategory);
        em.persist(subcategory);
        return subcategory;
    }

    public Category getCategory(Long categoryId) {
        return em.find(Category.class, categoryId);
    }

    public Subcategory getSubCategory(Long categoryId) {
        return em.find(Subcategory.class, categoryId);
    }

    public List<Category> getCategories() {
        return em.createNamedQuery(Category.GET_ALL, Category.class).getResultList();
    }

    public Collection<Category> getCategoriesWithSubcategories() {
        List<Category> categories = getCategories();
        categories.stream()
                .map(Category::getSubcategories)
                .flatMap(List::stream)
                .forEach(Subcategory::getName); // Make sure every subcategory is resolved
        return categories;
    }

    public List<Subcategory> getSubCategories() {
        return em.createNamedQuery(Subcategory.GET_ALL, Subcategory.class).getResultList();
    }

    public void deleteCategory(Long categoryId) {
        em.remove(getCategory(categoryId));
    }

    public List<Subcategory> getSubcategoryByParent(Long parentId) {
        return getCategoryWithSubcategories(parentId).getSubcategories();
    }

    public Category getCategoryWithSubcategories(Long categoryId) {
        Category category = getCategory(categoryId);
        category.getSubcategories().forEach(Subcategory::getName);
        return category;
    }
}
