package com.example.urlshortener.dto;

public class UrlResponse {

    private String shortUrl;
    private String longUrl;
    private Long totalClicks;

    public UrlResponse() {
    }

    public UrlResponse(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public UrlResponse(String shortUrl, String longUrl, Long totalClicks) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.totalClicks = totalClicks;
    }

    public String getShortUrl() { return shortUrl; }
    
    public String getLongUrl() { return longUrl; }

    public Long getTotalClicks() { return totalClicks; }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }
}