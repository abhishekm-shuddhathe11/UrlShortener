package com.example.urlshortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "url_visits",
    indexes = {
        @Index(name = "idx_visit_url_id", columnList = "url_id"),
        @Index(name = "idx_visit_visited_at", columnList = "visited_at")
    }
)
public class UrlVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "referrer", length = 2048)
    private String referrer;

    public UrlVisit() {
    }

    public UrlVisit(Url url, String ipAddress, String userAgent, String referrer) {
        this.url = url;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.visitedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Url getUrl() { return url; }

    public void setUrl(Url url) { this.url = url; }

    public LocalDateTime getVisitedAt() { return visitedAt; }

    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }

    public String getIpAddress() { return ipAddress; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }

    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getReferrer() { return referrer; }

    public void setReferrer(String referrer) { this.referrer = referrer; }
}
