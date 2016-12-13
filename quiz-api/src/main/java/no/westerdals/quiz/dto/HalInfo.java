package no.westerdals.quiz.dto;

public class HalInfo {
    public Href self;

    public HalInfo() {

    }

    public HalInfo(String self) {
        this.self = new Href(self);
    }

    public static class Href {
        public String href;

        public Href() {

        }

        public Href(String href) {
            this.href = href;
        }
    }
}
