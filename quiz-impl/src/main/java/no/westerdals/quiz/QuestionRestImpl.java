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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QuestionRestImpl implements QuestionRest {
    @Context
    private UriInfo uriInfo;

    @EJB
    private QuestionEJB questionEJB;

    private QuestionDtoConverter questionConverter = new QuestionDtoConverter();

    @Override
    public DtoList<QuestionDto> getQuestions(int offset, int results) {
        // TODO: Fix pagination
        return null;
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
