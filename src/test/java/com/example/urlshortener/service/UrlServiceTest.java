package com.example.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.entity.UrlVisit;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.repository.UrlVisitRepository;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlVisitRepository urlVisitRepository;

    @Captor
    private ArgumentCaptor<UrlVisit> urlVisitCaptor;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository, urlVisitRepository);
    }

    @Test
    void getOriginalUrlShouldIncrementClicksAndPersistVisit() {
        Url url = buildUrl("abc123", "https://example.com/long", 2L);
        when(urlRepository.findByShortKey("abc123")).thenReturn(Optional.of(url));

        String longUrl = urlService.getOriginalUrl("abc123", "127.0.0.1", "JUnit", "https://ref.example");

        assertEquals("https://example.com/long", longUrl);
        assertEquals(3L, url.getTotalClicks());
        verify(urlRepository).save(url);
        verify(urlVisitRepository).save(urlVisitCaptor.capture());
        UrlVisit visit = urlVisitCaptor.getValue();
        assertEquals("127.0.0.1", visit.getIpAddress());
        assertEquals("JUnit", visit.getUserAgent());
        assertEquals("https://ref.example", visit.getReferrer());
        assertEquals(url, visit.getUrl());
    }

    @Test
    void getAnalyticsShouldReturnAggregatedVisitMetrics() {
        Url url = buildUrl("stats01", "https://example.com/page", 5L);
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 20, 9, 30);
        LocalDateTime expiresAt = LocalDateTime.of(2026, 7, 20, 9, 30);
        ReflectionTestUtils.setField(url, "createdAt", createdAt);
        url.setExpiresAt(expiresAt);

        UrlVisit latestVisit = new UrlVisit(url, "10.0.0.1", "BrowserA", "https://ref1.example");
        latestVisit.setVisitedAt(LocalDateTime.of(2026, 6, 24, 11, 0));
        UrlVisit secondVisit = new UrlVisit(url, "10.0.0.2", "BrowserB", "https://ref2.example");
        secondVisit.setVisitedAt(LocalDateTime.of(2026, 6, 24, 10, 45));
        UrlVisit repeatedIpVisit = new UrlVisit(url, "10.0.0.1", "BrowserC", null);
        repeatedIpVisit.setVisitedAt(LocalDateTime.of(2026, 6, 24, 10, 15));

        when(urlRepository.findByShortKey("stats01")).thenReturn(Optional.of(url));
        when(urlVisitRepository.findByUrl_ShortKeyOrderByVisitedAtDesc("stats01"))
                .thenReturn(List.of(latestVisit, secondVisit, repeatedIpVisit));

        AnalyticsResponse response = urlService.getAnalytics("stats01", "http://localhost:9090");

        assertEquals("http://localhost:9090/stats01", response.getShortUrl());
        assertEquals("https://example.com/page", response.getLongUrl());
        assertEquals(5L, response.getTotalClicks());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(expiresAt, response.getExpiresAt());
        assertEquals(2L, response.getUniqueVisitorCount());
        assertEquals(latestVisit.getVisitedAt(), response.getLastVisitedAt());
        assertEquals(3, response.getVisits().size());
        assertNotNull(response.getVisits().get(0).getVisitedAt());
    }

    private Url buildUrl(String shortKey, String longUrl, Long totalClicks) {
        Url url = new Url();
        url.setShortKey(shortKey);
        url.setLongUrl(longUrl);
        url.setTotalClicks(totalClicks);
        return url;
    }
}
