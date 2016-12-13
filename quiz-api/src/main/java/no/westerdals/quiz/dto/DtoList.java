package no.westerdals.quiz.dto;

import java.util.List;

public class DtoList<T> {
    public List<T> list;

    public Integer rangeMin;
    public Integer rangeMax;
    public Integer totalSize;

    public ListHalInfo _links;

    public static class ListHalInfo extends HalInfo {
        public Href next;
        public Href previous;

        public ListHalInfo(String self, String next, String previous) {
            super(self);
            this.next = new Href(next);
            this.previous = new Href(previous);
        }


    }
}
