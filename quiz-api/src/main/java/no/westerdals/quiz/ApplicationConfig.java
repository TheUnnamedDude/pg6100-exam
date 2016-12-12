package no.westerdals.quiz;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    private final Set<Class<?>> classes = Collections.unmodifiableSet(Arrays.stream(
            new Class<?>[] {}).collect(Collectors.toSet()));

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
