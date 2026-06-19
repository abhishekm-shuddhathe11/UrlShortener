package com.example.urlshortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.urlshortener.entity.UrlVisit;

public interface UrlVisitRepository extends JpaRepository<UrlVisit, Long> {
    List<UrlVisit> findByUrl_ShortKeyOrderByVisitedAtDesc(String shortKey);
}
