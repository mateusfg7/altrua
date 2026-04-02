CREATE TABLE IF NOT EXISTS "events" (
    id UUID PRIMARY KEY,
    ong_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    cover_url VARCHAR(500),
    external_link VARCHAR(500),
    donation_info TEXT,
    donation_external_link VARCHAR(500),
    accepts_volunteers BOOLEAN NOT NULL DEFAULT FALSE,
    max_volunteers INTEGER,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address_label VARCHAR(255),
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,

    FOREIGN KEY (ong_id) REFERENCES ongs(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_active_event_slug ON events(slug) 
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_events_ong_id ON events(ong_id);

CREATE TABLE IF NOT EXISTS "tags" (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "event_tags" (
    event_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    
    PRIMARY KEY (event_id, tag_id),
    
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_event_tags_tag_id ON event_tags(tag_id);
