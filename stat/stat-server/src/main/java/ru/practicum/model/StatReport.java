package ru.practicum.model;

import lombok.Data;

@Data
public class StatReport {

        private String app;

        private String uri;

        private Long hits;

        public StatReport(String app, String uri, Long hits) {
            this.app = app;
            this.uri = uri;
            this.hits = hits;
        }
}
