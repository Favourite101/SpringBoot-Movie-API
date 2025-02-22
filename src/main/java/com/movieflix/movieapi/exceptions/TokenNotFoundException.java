package com.movieflix.movieapi.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {

        super(message);
    }
}
