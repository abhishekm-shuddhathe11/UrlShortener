-- Migration: Create url_visits table for analytics (Option B)
-- Run this script once against your PostgreSQL database.

CREATE TABLE IF NOT EXISTS url_visits (
    id          BIGSERIAL PRIMARY KEY,
    url_id      BIGINT        NOT NULL REFERENCES urls(id) ON DELETE CASCADE,
    visited_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(512),
    referrer    VARCHAR(2048)
);

CREATE INDEX IF NOT EXISTS idx_visit_url_id    ON url_visits (url_id);
CREATE INDEX IF NOT EXISTS idx_visit_visited_at ON url_visits (visited_at);
