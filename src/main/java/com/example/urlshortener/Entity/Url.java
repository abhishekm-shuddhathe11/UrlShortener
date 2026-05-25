package com.example.urlshortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

// Creates database index on shortKey column
// Improves short URL lookup performance
@Entity
@Table(
    name = "urls",
    indexes = {
        @Index(name = "idx_short_key", columnList = "shortKey")
    }
)         
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Primary key with auto-increment
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String shortKey;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Total number of times short URL was accessed
    @Column(nullable = false)
    private Long totalClicks = 0L;

    @Column
    private LocalDateTime expiresAt;

    // Constructor
    // public Url() {
    //     this.createdAt = LocalDateTime.now();
    // }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Getter for shortKey
    public String getShortKey() {
        return shortKey;
    }

    // Setter for shortKey
    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    // Getter for longUrl
    public String getLongUrl() {
        return longUrl;
    }

    // Setter for longUrl
    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    // Getter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}