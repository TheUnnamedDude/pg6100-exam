package no.westerdals.quiz.hysterix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import no.westerdals.quiz.dto.AnswerDto;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.dto.Quiz;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    public boolean checkAnswer(Long questionId, Long answerId) {
        return new CallCheckAnswer(questionId, answerId).execute();
    }

    private class CallGetRandom extends HystrixCommand<Quiz> {
        public CallGetRandom() {
            super(HystrixCommandGroupKey.Factory.asKey("Random question api"));
        }

        @Override
        protected Quiz run() throws Exception {
            QuestionDto questionDto = client
                    .target(uriBuilder.clone()
                            .path("quizzes")
                            .path("random")
                            .build())
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get()
                    .readEntity(QuestionDto.class);
            return new Quiz(questionDto.id, questionDto.text, questionDto.alternatives);
        }
    }

    private class CallCheckAnswer extends HystrixCommand<Boolean> {
        private final Long questionId;
        private final Long answerId;

        public CallCheckAnswer(Long questionId, Long answerId) {
            super(HystrixCommandGroupKey.Factory.asKey("Check answer"));
            this.questionId = questionId;
            this.answerId = answerId;
        }

        @Override
        protected Boolean run() throws Exception {
            Response post = client
                    .target(uriBuilder.clone()
                            .path("quizzes")
                            .path(questionId.toString())
                            .path("validate").build())
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(answerId, MediaType.APPLICATION_JSON));
            return post.readEntity(Boolean.class);
        }
    }
}
