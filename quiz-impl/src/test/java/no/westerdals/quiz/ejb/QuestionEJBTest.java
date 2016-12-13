package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Category;
import no.westerdals.quiz.entities.Question;
import no.westerdals.quiz.entities.Subcategory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class QuestionEJBTest extends EJBTestBase {

    @Test
    public void testCreateQuestion() throws Exception {
        Category category = categoryEJB.createCategory("Programming");
        Subcategory subcategory = categoryEJB.createSubcategory(category.getId(), "Java EE");
        Question question = questionEJB.createQuestion(subcategory.getId(), "Is this working?", "Yes", "no", "maybe", "who knows?");
        assertNotNull(question.getId());
    }
}
