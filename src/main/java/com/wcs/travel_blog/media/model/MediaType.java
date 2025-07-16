package com.wcs.travel_blog.media.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
        PHOTO("Photo"),
        VIDEO("Vidéo"),
        ;

        private final String code;

        MediaType(String code) {
                this.code = code;
        }

        @JsonValue
        public String getCode() {
                return code;
        }

        @JsonCreator
        public static MediaType fromCode(String code) {
                for (MediaType status : values()) {
                        if (status.code.equalsIgnoreCase(code)) {
                                return status;
                        }
                }
                throw new IllegalArgumentException("Type de média invalide : " + code);
        }
}