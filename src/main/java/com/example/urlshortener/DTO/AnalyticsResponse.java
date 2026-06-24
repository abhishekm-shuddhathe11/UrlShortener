package com.example.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AnalyticsResponse {

    private String shortUrl;
    private String longUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long uniqueVisitorCount;
    private LocalDateTime lastVisitedAt;
    private List<VisitEntry> visits;

    public AnalyticsResponse() {
    }

    public AnalyticsResponse(String shortUrl, String longUrl, Long totalClicks, LocalDateTime createdAt,
            LocalDateTime expiresAt, Long uniqueVisitorCount, LocalDateTime lastVisitedAt, List<VisitEntry> visits) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.totalClicks = totalClicks;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.uniqueVisitorCount = uniqueVisitorCount;
        this.lastVisitedAt = lastVisitedAt;
        this.visits = visits;
    }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public Long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(Long totalClicks) { this.totalClicks = totalClicks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Long getUniqueVisitorCount() { return uniqueVisitorCount; }
    public void setUniqueVisitorCount(Long uniqueVisitorCount) { this.uniqueVisitorCount = uniqueVisitorCount; }

    public LocalDateTime getLastVisitedAt() { return lastVisitedAt; }
    public void setLastVisitedAt(LocalDateTime lastVisitedAt) { this.lastVisitedAt = lastVisitedAt; }

    public List<VisitEntry> getVisits() { return visits; }
    public void setVisits(List<VisitEntry> visits) { this.visits = visits; }

    public static class VisitEntry {
        private LocalDateTime visitedAt;
        private String ipAddress;
        private String userAgent;
        private String referrer;

        public VisitEntry() {
        }

        public VisitEntry(LocalDateTime visitedAt, String ipAddress, String userAgent, String referrer) {
            this.visitedAt = visitedAt;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.referrer = referrer;
        }

        public LocalDateTime getVisitedAt() { return visitedAt; }
        public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
    }
}
