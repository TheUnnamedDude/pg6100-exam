package no.westerdals.quiz;

import no.westerdals.quiz.dto.DtoList;
import no.westerdals.quiz.dto.QuestionDto;
import no.westerdals.quiz.converters.QuestionDtoConverter;
import no.westerdals.quiz.ejb.QuestionEJB;
import no.westerdals.quiz.entities.Question;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QuestionRestImpl implements QuestionRest {
    @Context
    private UriInfo uriInfo;

    @EJB
    private QuestionEJB questionEJB;

    private QuestionDtoConverter questionConverter = new QuestionDtoConverter();

    @Override
    public DtoList<QuestionDto> getQuestions(int offset, int results, Long filter) {
        Long size;
        List<Question> questions;
        if (filter == null) {
            size = questionEJB.getNumberOfQuestions();
            questions = questionEJB.getQuestions(offset, results);
        } else {
            List<Question> result = questionEJB.getQuestionsByCategory(filter);
            size = (long) result.size();
            questions = result.stream().skip(offset).limit(results).collect(Collectors.toList());
        }
        String self = uriInfo.getAbsolutePathBuilder()
                .queryParam("offset", offset)
                .queryParam("results", results)
                .build()
                .toString();
        String next = uriInfo.getAbsolutePathBuilder()
                .queryParam("offset", offset + results)
                .queryParam("results", results)
                .build()
                .toString();
        String previous = uriInfo.getAbsolutePathBuilder()
                .queryParam("offset", offset - results)
                .queryParam("results", results)
                .build()
                .toString();
        return questionConverter.convertHAL(questions, (long) offset, size, self, next, previous);
    }

    @Override
    public Response createQuestion(QuestionDto questionDto) {
        String[] alternatives = questionDto.alternatives.stream().map(answer -> answer.text).toArray(String[]::new);
        Question question = questionEJB.createQuestion(questionDto.category.id, questionDto.text,
                questionDto.answer.text, alternatives);
        return Response.created(uriInfo.getBaseUriBuilder().path(ENDPOINT).path(question.getId().toString()).build()).build();
    }

    @Override
    public QuestionDto getQuestion(Long id) {
        return questionConverter.convert(questionEJB.getQuestion(id));
    }

    @Override
    public QuestionDto getRandomQuestion() {
        return questionConverter.convert(questionEJB.getRandomQuestion());
    }
}
