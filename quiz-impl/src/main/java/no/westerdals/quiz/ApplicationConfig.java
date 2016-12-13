package no.westerdals.quiz;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    private final Set<Class<?>> classes;

    public ApplicationConfig() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(CategoryRestImpl.class);
        classes.add(QuestionRestImpl.class);
        this.classes = Collections.unmodifiableSet(classes);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
