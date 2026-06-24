package com.example.urlshortener.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.UrlVisit;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.repository.UrlVisitRepository;
import com.example.urlshortener.util.Base62;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlVisitRepository urlVisitRepository;

    public UrlService(UrlRepository urlRepository, UrlVisitRepository urlVisitRepository) {
        this.urlRepository = urlRepository;
        this.urlVisitRepository = urlVisitRepository;
    }

   @Transactional
    public String shortenUrl(String longUrl,String customShortKey,LocalDateTime expiresAt) {

    validateLongUrl(longUrl);

    // Return existing short key if URL already exists
    Optional<Url> existingUrl = urlRepository.findByLongUrl(longUrl);

    if (existingUrl.isPresent()) {
        return existingUrl.get().getShortKey();
    }

    // Custom short key flow
    if (StringUtils.hasText(customShortKey)) 
    {

        if (urlRepository.findByShortKey(customShortKey).isPresent()) 
        {
            throw new IllegalArgumentException(
                    "Custom short key already exists: " + customShortKey);
        }

            Url url = new Url();
            url.setLongUrl(longUrl);
            url.setShortKey(customShortKey);
            url.setExpiresAt(expiresAt);

            urlRepository.save(url);

            return customShortKey;
        }

        // Auto-generated short key flow
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setExpiresAt(expiresAt);

        // Persist once to obtain the generated ID while satisfying NOT NULL on shortKey.
        String tempShortKey = "tmp" + UUID.randomUUID().toString().replace("-", "");
        url.setShortKey(tempShortKey);

        Url saved = urlRepository.save(url);

        String shortKey = Base62.encode(saved.getId());

        saved.setShortKey(shortKey);

        urlRepository.save(saved);

        return shortKey;
    }

    @Transactional
    public String getOriginalUrl(String shortKey, String ipAddress, String userAgent, String referrer) {

        Url url = getActiveUrl(shortKey);

        Long clicks = url.getTotalClicks();
        if (clicks == null) {
            clicks = 0L;
        }
        url.setTotalClicks(clicks + 1);

        urlRepository.save(url);
        UrlVisit visit = new UrlVisit(url, ipAddress, userAgent, referrer);
        urlVisitRepository.save(visit);

        return url.getLongUrl();
    }

    public String getOriginalUrl(String shortKey) {
        return getOriginalUrl(shortKey, null, null, null);
    }

    public String getOriginalUrlForInfo(String shortKey) {
        Url url = getActiveUrl(shortKey);
        return url.getLongUrl();
    }

    public Url getUrlDetails(String shortKey) {
        return getActiveUrl(shortKey);
    }

    public AnalyticsResponse getAnalytics(String shortKey, String baseUrl) {
        Url url = getActiveUrl(shortKey);
        List<UrlVisit> visits = urlVisitRepository.findByUrl_ShortKeyOrderByVisitedAtDesc(shortKey);
        List<AnalyticsResponse.VisitEntry> entries = visits.stream()
                .map(v -> new AnalyticsResponse.VisitEntry(
                        v.getVisitedAt(), v.getIpAddress(), v.getUserAgent(), v.getReferrer()))
                .collect(Collectors.toList());
        long uniqueVisitorCount = visits.stream()
                .map(UrlVisit::getIpAddress)
                .filter(StringUtils::hasText)
                .distinct()
                .count();
        LocalDateTime lastVisitedAt = visits.isEmpty() ? null : visits.get(0).getVisitedAt();
        Long totalClicks = url.getTotalClicks() == null ? 0L : url.getTotalClicks();

        String shortUrl = baseUrl + "/" + shortKey;
        return new AnalyticsResponse(
                shortUrl,
                url.getLongUrl(),
                totalClicks,
                url.getCreatedAt(),
                url.getExpiresAt(),
                uniqueVisitorCount,
                lastVisitedAt,
                entries);
    }

    private Url getActiveUrl(String shortKey) {

        if (!StringUtils.hasText(shortKey)) {
            throw new IllegalArgumentException("Short key must not be blank");
        }

        Url url = urlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException(shortKey));

        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException();
        }

        if (!StringUtils.hasText(url.getLongUrl())) {
            throw new IllegalArgumentException("Stored long URL is invalid");
        }

        return url;
    }

    private static void validateLongUrl(String longUrl) {
        if (!StringUtils.hasText(longUrl)) {
            throw new IllegalArgumentException("URL must not be blank");
        }
    }
}