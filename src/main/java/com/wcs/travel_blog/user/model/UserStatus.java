package com.wcs.travel_blog.user.model;


import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    BLOCKED("blocked");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getValue() {
        return label;
    }

}
