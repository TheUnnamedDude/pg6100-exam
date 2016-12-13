package no.westerdals.quiz.ejb;

import no.westerdals.quiz.entities.Answer;
import no.westerdals.quiz.entities.Question;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Stateless
public class QuestionEJB {
    @PersistenceContext
    private EntityManager em;
    @EJB
    private CategoryEJB categoryEJB;

    private final Random random = new Random();

    public Question createQuestion(Long subcategoryId, String text, String answer, String... incorrectAnswers) {
        Question question = new Question();
        List<Answer> answers = Arrays.stream(incorrectAnswers)
                .map(s -> createAnswer(s, question))
                .collect(Collectors.toList());
        question.setText(text);
        question.setCorrectAnswer(createAnswer(answer, question));
        question.setIncorrectAnswers(answers);
        question.setSubcategory(categoryEJB.getSubCategory(subcategoryId));
        em.persist(question);
        return question;
    }

    public Question getQuestion(Long questionId) {
        return em.find(Question.class, questionId);
    }

    public List<Question> getQuestions() {
        return em.createNamedQuery(Question.GET_ALL, Question.class).getResultList();
    }

    public Long getNumberOfQuestions() {
        return em.createNamedQuery(Question.GET_SIZE, Long.class).getSingleResult();
    }

    public Question updateQuestion(Long questionId, String text, String answer, String... incorrectAnswers) {
        Question question = getQuestion(questionId);
        if (text != null) {
            question.setText(text);
        }
        if (answer != null) {
            question.getCorrectAnswer().setText(answer);
        }
        if (incorrectAnswers != null) {
            question.getIncorrectAnswers().clear();
            List<Answer> answers = Arrays.stream(incorrectAnswers)
                    .map(s -> createAnswer(s, question))
                    .collect(Collectors.toList());
            question.setIncorrectAnswers(answers);
        }
        return question;
    }

    public Question getRandomQuestion() {
        TypedQuery<Question> query = em.createNamedQuery(Question.GET_ALL, Question.class);
        query.setFirstResult(random.nextInt(getNumberOfQuestions().intValue()));
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    private Answer createAnswer(String text, Question question) {
        Answer answer = new Answer();
        answer.setText(text);
        answer.setQuestion(question);
        return answer;
    }

    public List<Question> getQuestions(int offset, int results) {
        return em.createNamedQuery(Question.GET_ALL, Question.class)
                .setFirstResult(offset)
                .setMaxResults(results)
                .getResultList();
    }

    public List<Question> getQuestionsByCategory(Long subcategoryId) {
        return em.createNamedQuery(Question.GET_ALL_BY_SUBCATEGORY, Question.class)
                .setParameter("subcategoryId", subcategoryId)
                .getResultList();
    }
}
