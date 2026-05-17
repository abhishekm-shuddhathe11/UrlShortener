package com.example.urlshortener.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.Base62;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

   @Transactional
public String shortenUrl(String longUrl, String customShortKey) {

    validateLongUrl(longUrl);

    // Return existing short key if URL already exists
    Optional<Url> existingUrl = urlRepository.findByLongUrl(longUrl);

    if (existingUrl.isPresent()) {
        return existingUrl.get().getShortKey();
    }

    // Custom short key flow
    if (StringUtils.hasText(customShortKey)) {

        if (urlRepository.findByShortKey(customShortKey).isPresent()) {
            throw new IllegalArgumentException(
                    "Custom short key already exists: " + customShortKey);
        }

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortKey(customShortKey);

        urlRepository.save(url);

        return customShortKey;
    }

    // Auto-generated short key flow
    Url url = new Url();
    url.setLongUrl(longUrl);

    Url saved = urlRepository.save(url);

    String shortKey = Base62.encode(saved.getId());

    saved.setShortKey(shortKey);

    urlRepository.save(saved);

    return shortKey;
}

    public String getOriginalUrl(String shortKey) {

        if (!StringUtils.hasText(shortKey)) {
            throw new IllegalArgumentException("Short key must not be blank");
        }

        Url url = urlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException(shortKey));

        Long clicks = url.getTotalClicks();
        if (clicks == null) clicks = 0L;

        url.setTotalClicks(clicks + 1);
        urlRepository.save(url);

        return url.getLongUrl();
    }

    private static void validateLongUrl(String longUrl) {
        if (!StringUtils.hasText(longUrl)) {
            throw new IllegalArgumentException("URL must not be blank");
        }
    }
}