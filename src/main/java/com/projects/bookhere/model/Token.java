package com.projects.bookhere.model;

/*To return token to front end in JSON format, not stored in database */
public class Token {
    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
