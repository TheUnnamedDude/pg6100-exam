package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Question;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class QuestionEJBTest extends EJBTestBase {

    @Test
    public void testCreateQuestion() throws Exception {
        Question question = questionEJB.createAnswer("Is this working?", "Yes", "no", "maybe", "who knows?");
        assertNotNull(question.getId());
    }
}
