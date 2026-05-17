package com.example.urlshortener.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlRequest {

    @NotBlank(message = "URL must not be blank")
    @Pattern(
    regexp = "^(http://|https://).+$",
    message = "URL must start with http:// or https://"
    )
    private String url;

    @Size(min = 3, max = 10, message = "Short key must be between 3 and 10 characters")
    private String shortKey;
    
    public UrlRequest() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }
}
