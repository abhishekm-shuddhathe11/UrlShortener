package com.example.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public UrlResponse shorten(@Valid @RequestBody UrlRequest request)
    {
        String longUrl = request.getUrl();
        String customShortKey = request.getShortKey();
        String shortKey = service.shortenUrl(longUrl, customShortKey);

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{shortKey}")
                .buildAndExpand(shortKey)
                .toUriString();

        return new UrlResponse(shortUrl, longUrl);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {

        String longUrl = service.getOriginalUrl(shortKey);

        return ResponseEntity
                .status(302)
                .location(java.net.URI.create(longUrl))
                .build();
    }

    @GetMapping("/info/{shortKey}")
    public UrlResponse info(@PathVariable String shortKey) {
        String longUrl = service.getOriginalUrl(shortKey);
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{shortKey}")
                .buildAndExpand(shortKey)
                .toUriString();

        return new UrlResponse(shortUrl, longUrl);
    }
    
    }