package com.example.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

    @Tag(
        name = "URL Shortener APIs",
        description = "APIs for shortening and redirecting URLs"
    )
    @RestController
    public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @Operation(summary = "Create short URL")
    @PostMapping("/api/v1/shorten")
    public UrlResponse shorten(@Valid @RequestBody UrlRequest request)
    {
        final String longUrl = request.getUrl();
        final String customShortKey = request.getShortKey();
        final String shortKey = service.shortenUrl(longUrl, customShortKey, request.getExpiresAt());  

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{shortKey}")
                .buildAndExpand(shortKey)
                .toUriString();

        return new UrlResponse(shortUrl, longUrl);
    }

    @Operation(
    summary = "Redirect to original URL",
    description = "Redirects users from a short URL to the stored original URL")
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey, HttpServletRequest request) {

        String ipAddress = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");

        String longUrl = service.getOriginalUrl(shortKey, ipAddress, userAgent, referrer);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(java.net.URI.create(longUrl))
                .build();
    }

    @Operation(
    summary = "Get URL information",
    description = "Returns details for a shortened URL")
    @GetMapping("/api/v1/info/{shortKey}")
    public UrlResponse info(@PathVariable String shortKey) {
        com.example.urlshortener.entity.Url url = service.getUrlDetails(shortKey);
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{shortKey}")
                .buildAndExpand(shortKey)
                .toUriString();

        return new UrlResponse(shortUrl, url.getLongUrl(), url.getTotalClicks());
    }

    @Operation(
    summary = "Get analytics for a short URL",
    description = "Returns click count and detailed visit logs (timestamp, IP, User-Agent, referrer)")
    @GetMapping("/api/v1/analytics/{shortKey}")
    public AnalyticsResponse analytics(@PathVariable String shortKey) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        return service.getAnalytics(shortKey, baseUrl);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    }