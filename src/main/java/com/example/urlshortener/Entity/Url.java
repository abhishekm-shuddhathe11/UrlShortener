package com.example.urlshortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity                         // Marks this class as a database entity
@Table(name = "urls")          // Table name in PostgreSQL
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Primary key with auto-increment
    private Long id;

    @Column(unique = true)
    private String shortKey;

    @Column(nullable = false)
    // Original long URL
    private String longUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Total number of times short URL was accessed
    @Column(nullable = false)
    private Long totalClicks = 0L;

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
}