package com.example.urlshortener.exception;

public class UrlExpiredException extends RuntimeException {

    public UrlExpiredException() {
        super("Short URL has expired");
    }
}