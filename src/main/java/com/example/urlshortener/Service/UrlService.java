package com.example.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.Base62;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
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
        String tempShortKey = "t" + UUID.randomUUID().toString().replace("-", "").substring(0, 9);
        url.setShortKey(tempShortKey);

        Url saved = urlRepository.save(url);

        String shortKey = Base62.encode(saved.getId());

        saved.setShortKey(shortKey);

        urlRepository.save(saved);

        return shortKey;
    }

    public String getOriginalUrl(String shortKey) {

        Url url = getActiveUrl(shortKey);

        Long clicks = url.getTotalClicks();

        if (clicks == null) {
            clicks = 0L;
        }

        url.setTotalClicks(clicks + 1);

        // Redirect should still work even if click tracking update fails for legacy rows.
        try {
            urlRepository.save(url);
        } catch (RuntimeException ex) {
            log.warn("Failed to update click count for short key {}", shortKey, ex);
        }

        return url.getLongUrl();
    }

    public String getOriginalUrlForInfo(String shortKey) {

        Url url = getActiveUrl(shortKey);

        return url.getLongUrl();
    }

    private Url getActiveUrl(String shortKey) {

        if (!StringUtils.hasText(shortKey)) {
            throw new IllegalArgumentException("Short key must not be blank");
        }

        Url url = urlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException(shortKey));

        // Check expiry
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