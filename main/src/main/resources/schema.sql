DROP TABLE IF EXISTS users, categories, locations, compilations, events, requests, compilations_event, comments;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(310) NOT NULL,
    email VARCHAR(310) NOT NULL,
    CONSTRAINT user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(310) NOT NULL,
    CONSTRAINT category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        BIGINT                      NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    location_id        BIGINT                      NOT NULL REFERENCES locations (id) ON DELETE CASCADE,
    paid               BOOLEAN                     NOT NULL,
    participant_limit  BIGINT                      NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN                     NOT NULL,
    state              VARCHAR(24)                 NOT NULL,
    title              VARCHAR(120)                NOT NULL,
    confirmed_requests BIGINT
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_on   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id     BIGINT                      NOT NULL REFERENCES events (id),
    requester_id BIGINT                      NOT NULL REFERENCES users (id),
    status       VARCHAR(64)                 NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title  VARCHAR(310) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_event
(
    compilation_id BIGINT NOT NULL REFERENCES compilations (id) ON UPDATE CASCADE ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES events (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id         BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text       VARCHAR(2000)               NOT NULL,
    author_id  BIGINT                      NOT NULL REFERENCES users (id),
    event_id   BIGINT                      NOT NULL REFERENCES events (id),
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITHOUT TIME ZONE
);