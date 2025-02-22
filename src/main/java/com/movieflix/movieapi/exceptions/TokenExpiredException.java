package com.movieflix.movieapi.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {

        super(message);
    }
}
