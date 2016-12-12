package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Category;
import no.westerdals.quiz.entities.SubCategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public List<SubCategory> getSubCategories() {
        return em.createNamedQuery(SubCategory.GET_ALL, SubCategory.class).getResultList();
    }
}
