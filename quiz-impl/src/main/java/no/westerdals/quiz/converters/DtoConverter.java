package no.westerdals.quiz.converters;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface DtoConverter<Entity, Dto> {
    Dto convert(Entity entity);

    default List<Dto> convert(Collection<Entity> entities) {
        return entities.stream().map(this::convert).collect(Collectors.toList());
    }
}
