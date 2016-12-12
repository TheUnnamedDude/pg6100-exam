package no.westerdals.quiz.ejb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import javax.ejb.EJB;

public class EJBTestBase {

    @EJB
    CategoryEJB categoryEJB;
    @EJB
    QuestionEJB questionEJB;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "no.westerdals.quiz")
                .addAsResource("META-INF/persistence.xml");
    }
}
