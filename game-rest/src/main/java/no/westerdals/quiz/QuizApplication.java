package no.westerdals.quiz;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.westerdals.quiz.api.QuizResource;
import no.westerdals.quiz.hysterix.QuizRest;

public class QuizApplication extends Application<QuizConfiguration> {
    public static void main(String[] args) throws Exception {
        new QuizApplication().run(args);
    }

    @Override
    public void run(QuizConfiguration quizConfiguration, Environment environment) throws Exception {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.0.1");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/game/api");
        beanConfig.setResourcePackage("no.westerdals.quiz.api");

        beanConfig.setScan(true);

        final QuizRest quizRest = new QuizRest("http://localhost:8090/quiz/api");
        final QuizResource quizResource = new QuizResource(quizRest);
        environment.jersey().setUrlPattern("/game/*");
        environment.jersey().register(quizResource);
        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(new SwaggerSerializers());
    }

    @Override
    public void initialize(Bootstrap<QuizConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public String getName() {
        return "quiz-rest";
    }
}
