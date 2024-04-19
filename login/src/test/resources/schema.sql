CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS public.user (
    id                  UUID NOT NULL,
    alias               VARCHAR(64),
    created_at          TIMESTAMP   NOT NULL,
    last_modified_at    TIMESTAMP   NOT NULL,
    last_login_at       TIMESTAMP,
    state               VARCHAR(12) NOT NULL DEFAULT 'ACTIVATED',
    roles               VARCHAR(128)[] NOT NULL DEFAULT '{USER}',
    github              VARCHAR(16),
    google              VARCHAR(24),
    PRIMARY KEY(id)
);

INSERT INTO public.user (id, alias, created_at, last_modified_at, last_login_at, roles)
VALUES ('a8e8a479-03b4-492c-ba73-d4bf7e0892d7', 'test-admin', '1900-01-01T01:00:00', '1900-01-01T01:00:00', null, '{ADMIN,USER}');



