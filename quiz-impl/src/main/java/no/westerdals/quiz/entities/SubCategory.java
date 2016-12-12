package no.westerdals.quiz.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(
                name = SubCategory.GET_ALL,
                query = "select c from SubCategory as c"
        )
})
@Entity
public class SubCategory {
    public static final String GET_ALL = "SubCategory#getAll";

    @GeneratedValue
    @Id
    private Long id;

    @NotNull
    @Min(4)
    private String name;

    @NotNull
    @ManyToOne
    private Category parent;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
}
