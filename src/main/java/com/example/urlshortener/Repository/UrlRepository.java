package com.example.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.urlshortener.entity.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortKey(String shortKey);
    Optional<Url> findByLongUrl(String longUrl);
}
