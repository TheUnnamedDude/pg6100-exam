package no.westerdals.quiz.hysterix;

import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.dto.Quiz;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.InputStreamReader;
import java.net.URI;

public class QuizRest {
    private final UriBuilder uriBuilder;
    private final Gson GSON = new Gson();
    CloseableHttpClient httpClient = HttpClients.createDefault();

    public QuizRest(String baseUrl) {
        uriBuilder = UriBuilder.fromUri(baseUrl);
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
            URI uri = uriBuilder.clone()
                    .path("quizzes")
                    .path("random")
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader("Accept", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            QuestionDto questionDto = GSON.fromJson(new InputStreamReader(response.getEntity().getContent()), QuestionDto.class);
            response.close();
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
            URI uri = uriBuilder.clone()
                    .path("quizzes")
                    .path(questionId.toString())
                    .path("validate").build();
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(GSON.toJson(answerId)));
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            CloseableHttpResponse response = httpClient.execute(post);
            Boolean result = GSON.fromJson(new InputStreamReader(response.getEntity().getContent()), Boolean.class);
            response.close();
            return result;
        }
    }
}
