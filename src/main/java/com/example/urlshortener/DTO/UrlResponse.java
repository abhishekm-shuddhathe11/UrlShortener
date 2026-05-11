package com.example.urlshortener.dto;

public class UrlResponse {

    private String shortUrl;
    private String longUrl;

    public UrlResponse() {
    }

    public UrlResponse(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public String getShortUrl() { return shortUrl; }
    
    public String getLongUrl() { return longUrl; }
}