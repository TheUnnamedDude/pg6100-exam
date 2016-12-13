package no.westerdals.quiz.hysterix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.dto.Quiz;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class QuizRest {
    private final UriBuilder uriBuilder;
    private final Client client;

    public QuizRest(String baseUrl) {
        uriBuilder = UriBuilder.fromUri(baseUrl);
        client = ClientBuilder.newClient();
    }

    public Quiz getRandomQuiz() {
        return new CallGetRandom().execute();
    }

    private class CallGetRandom extends HystrixCommand<Quiz> {
        public CallGetRandom() {
            super(HystrixCommandGroupKey.Factory.asKey("Random question api"));
        }

        @Override
        protected Quiz run() throws Exception {
            System.out.println("===" + uriBuilder.path("quizzes").path("random").build());
            QuestionDto questionDto = client
                    .target(uriBuilder.path("quizzes").path("random").build())
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get()
                    .readEntity(QuestionDto.class);
            return new Quiz(questionDto.id, questionDto.text, questionDto.alternatives);
        }
    }
}
