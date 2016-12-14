package no.westerdals.quiz;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.westerdals.quiz.api.QuizResource;
import no.westerdals.quiz.hysterix.QuizRest;

public class QuizApplication extends Application<QuizConfiguration> {
    public static void main(String[] args) throws Exception {
        new QuizApplication().run(args);
    }

    @Override
    public void run(QuizConfiguration quizConfiguration, Environment environment) throws Exception {
        final QuizRest quizRest = new QuizRest("http://localhost:8090/quiz/api");
        final QuizResource quizResource = new QuizResource(quizRest);
        environment.jersey().register(quizResource);
    }

    @Override
    public void initialize(Bootstrap<QuizConfiguration> bootstrap) {

    }

    @Override
    public String getName() {
        return "quiz-rest";
    }
}
