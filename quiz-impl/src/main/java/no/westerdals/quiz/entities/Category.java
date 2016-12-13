package no.westerdals.quiz.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = Category.GET_ALL,
                query = "select c from Category as c"
        )
})
@Entity
public class Category {
    public static final String GET_ALL = "Category#getAll";
    @GeneratedValue
    @Id
    private Long id;
    @NotNull
    @Size(min = 4)
    private String text;
    @OneToMany(mappedBy = "parent")
    private List<Subcategory> subcategories;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubcategories(List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }
}
