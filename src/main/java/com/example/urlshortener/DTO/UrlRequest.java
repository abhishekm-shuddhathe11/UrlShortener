package com.example.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlRequest {

    @NotBlank(message = "URL must not be blank")
    @Pattern(
    regexp = "^(http://|https://).+$",
    message = "URL must start with http:// or https://"
    )
    @Schema(description = "Original long URL")
    private String url;

    @Size(min = 3, max = 10, message = "Short key must be between 3 and 10 characters")
    @Schema(description = "Optional custom short key")
    private String shortKey;

    @Schema(description = "Optional expiration date and time for the short URL")
    private LocalDateTime expiresAt;
    
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

    public LocalDateTime getExpiresAt() {
    return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
