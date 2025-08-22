package com.leopaul29.bento.entities;

import lombok.Getter;

// Role.java
@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    MODERATOR("ROLE_MODERATOR");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

}