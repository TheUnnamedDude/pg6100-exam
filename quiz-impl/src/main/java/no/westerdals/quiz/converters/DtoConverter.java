package no.westerdals.quiz.converters;

import no.westerdals.quiz.dto.DtoList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface DtoConverter<Entity, Dto> {
    Dto convert(Entity entity);

    default List<Dto> convert(Collection<Entity> entities) {
        return entities.stream().map(this::convert).collect(Collectors.toList());
    }

    default DtoList<Dto> convertHAL(Collection<Entity> entities, Long startIndex, Long totalEntries,
                                    String self, String next, String previous) {
        DtoList<Dto> result = new DtoList<>();
        result.list = convert(entities);
        result.rangeMin = startIndex;
        result.rangeMax = startIndex + entities.size();
        result.totalSize = totalEntries;
        result._links = new DtoList.ListHalInfo(self, next, previous);
        return result;
    }
}
