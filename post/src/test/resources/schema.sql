CREATE SCHEMA IF NOT EXISTS public;

create table public."user" (
                               id uuid primary key not null,
                               alias character varying(64),
                               created_at timestamp without time zone not null,
                               last_modified_at timestamp without time zone not null,
                               last_login_at timestamp without time zone,
                               state character varying(12) not null default 'ACTIVATED',
                               roles               VARCHAR(128)[] NOT NULL DEFAULT '{USER}',
                               github character varying(16),
                               google character varying(24)
);
create unique index uk1vud26mbr85wpxylua9or6m6g on "user" using btree (alias);

INSERT INTO public.user (id, alias, created_at, last_modified_at, last_login_at, roles)
VALUES ('a8e8a479-03b4-492c-ba73-d4bf7e0892d7', 'test-admin', '1900-01-01T01:00:00', '1900-01-01T01:00:00', null, '{ADMIN,USER}');

create table public.post (
                             id uuid primary key not null,
                             author uuid not null,
                             title text not null,
                             created_at timestamp(6) without time zone not null,
                             updated_at timestamp(6) without time zone,
                             published_at timestamp(6) without time zone,
                             description text,
                             markdown text not null,
                             html text not null,
                             thumbnail_url text,
                             github_url text,
                             foreign key (author) references public."user" (id)
                                 match simple on update no action on delete no action
);
create index idx2jm25hjrq6iv4w8y1dhi0d9p4 on post using btree (title);
create index idxpwh7iy9o4qv751bn9mb25fa44 on post using btree (description);
create index idxlh2uuuhcoitjk4qu5ausofu05 on post using btree (published_at);
create unique index uk83qskhpmb8u7odx43h7i9ar2t on post using btree (author, title);

create table public.post_keyword (
                                     post uuid not null,
                                     keyword text not null,
                                     primary key (post, keyword),
                                     foreign key (post) references public.post (id)
                                         match simple on update no action on delete no action
);
create index idxqjil61hvlpbisgs3c2d16x8wp on post_keyword using btree (post);
create index idxsg9yvmi91w5vkboo7844ti1op on post_keyword using btree (keyword);

create view public.catalog
            (id, title, created_at, updated_at, author, author_alias, html, markdown, thumbnail_url, github_url,
             description, tags, published_at)
as
SELECT p.id,
       p.title,
       p.created_at,
       p.updated_at,
       p.author,
       u.alias    AS author_alias,
       p.html,
       p.markdown,
       p.thumbnail_url,
       p.github_url,
       p.description,
       k.keywords AS tags,
       p.published_at
FROM post p,
     "user" u,
     (SELECT p_1.id,
             array_remove(array_agg(k_1.keyword), NULL::text) AS keywords
      FROM post p_1
               LEFT JOIN post_keyword k_1 ON p_1.id = k_1.post
      GROUP BY p_1.id) k
WHERE u.id = p.author
  AND p.id = k.id;





