package com.example.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        String longUrl = request.getUrl();
        String customShortKey = request.getShortKey();
        String shortKey = service.shortenUrl(longUrl, customShortKey,request.getExpiresAt());   

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
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {

        String longUrl = service.getOriginalUrl(shortKey);

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
        String longUrl = service.getOriginalUrlForInfo(shortKey);
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{shortKey}")
                .buildAndExpand(shortKey)
                .toUriString();

        return new UrlResponse(shortUrl, longUrl);
    }
    
    }