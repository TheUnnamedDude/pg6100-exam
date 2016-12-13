package no.westerdals.quiz.dto;

public class HalInfo {
    public final Href self;

    public HalInfo(String self) {
        this.self = new Href(self);
    }

    public static class Href {
        public final String href;

        public Href(String href) {
            this.href = href;
        }
    }
}
