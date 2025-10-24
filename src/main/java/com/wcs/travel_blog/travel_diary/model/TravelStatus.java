package com.wcs.travel_blog.travel_diary.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TravelStatus {
        IN_PROGRESS("IN_PROGRESS"),
        COMPLETED("COMPLETED"),
        DISABLED("DISABLED"),
        ;

        private final String code;

        TravelStatus(String code) {
                this.code = code;
        }

        @JsonValue
        public String getCode() {
                return code;
        }

        @JsonCreator
        public static TravelStatus fromCode(String code) {
                for (TravelStatus status : values()) {
                        if (status.code.equalsIgnoreCase(code)) {
                                return status;
                        }
                }
                throw new IllegalArgumentException("Status invalide : " + code);
        }
}