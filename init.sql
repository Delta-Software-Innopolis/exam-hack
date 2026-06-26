BEGIN;

CREATE TABLE alembic_version (
    version_num VARCHAR(32) NOT NULL, 
    CONSTRAINT alembic_version_pkc PRIMARY KEY (version_num)
);

-- Running upgrade  -> 75fd25934662

INSERT INTO alembic_version (version_num) VALUES ('75fd25934662') RETURNING alembic_version.version_num;

-- Running upgrade 75fd25934662 -> f45a2233727a

UPDATE alembic_version SET version_num='f45a2233727a' WHERE alembic_version.version_num = '75fd25934662';

-- Running upgrade f45a2233727a -> 471509bc311b

CREATE TABLE users (
    id SERIAL NOT NULL, 
    name VARCHAR(30) NOT NULL, 
    pass_hash TEXT NOT NULL, 
    PRIMARY KEY (id)
);

CREATE INDEX ix_users_id ON users (id);

CREATE INDEX ix_users_name ON users (name);

CREATE INDEX ix_users_pass_hash ON users (pass_hash);

UPDATE alembic_version SET version_num='471509bc311b' WHERE alembic_version.version_num = 'f45a2233727a';

-- Running upgrade 471509bc311b -> fe2ebf3f76c2

CREATE TABLE packs (
    id SERIAL NOT NULL, 
    name VARCHAR(50) NOT NULL, 
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL, 
    updating_date TIMESTAMP WITH TIME ZONE, 
    author_id INTEGER NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(author_id) REFERENCES users (id)
);

CREATE INDEX ix_packs_id ON packs (id);

CREATE INDEX ix_packs_name ON packs (name);

CREATE INDEX ix_packs_author_id ON packs (author_id);

CREATE TABLE pack_permissions (
    user_id INTEGER NOT NULL, 
    pack_id INTEGER NOT NULL, 
    permission INTEGER NOT NULL,
    
    PRIMARY KEY (user_id, pack_id), 
    FOREIGN KEY(user_id) REFERENCES users (id), 
    FOREIGN KEY(pack_id) REFERENCES packs (id)
);

CREATE INDEX ix_pack_permissions_user_id ON pack_permissions (user_id);

CREATE INDEX ix_pack_permissions_pack_id ON pack_permissions (pack_id);

CREATE INDEX ix_pack_permissions_permission ON pack_permissions (permission);

CREATE TABLE cards (
    id SERIAL NOT NULL, 
    question TEXT NOT NULL, 
    pack_id INTEGER NOT NULL, 
    hint TEXT NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(pack_id) REFERENCES packs (id)
);

CREATE INDEX ix_cards_id ON cards (id);

CREATE INDEX ix_cards_pack_id ON cards (pack_id);

CREATE TABLE comments (
    id SERIAL NOT NULL, 
    user_id INTEGER NOT NULL, 
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL, 
    updating_date TIMESTAMP WITH TIME ZONE, 
    pack_id INTEGER NOT NULL, 
    content TEXT NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(pack_id) REFERENCES packs (id), 
    FOREIGN KEY(user_id) REFERENCES users (id)
);

CREATE INDEX ix_comments_id ON comments (id);

CREATE INDEX ix_comments_pack_id ON comments (pack_id);

CREATE TABLE forks (
    fork_id INTEGER NOT NULL, 
    original_id INTEGER NOT NULL, 
    PRIMARY KEY (fork_id, original_id), 
    FOREIGN KEY(fork_id) REFERENCES packs (id), 
    FOREIGN KEY(original_id) REFERENCES packs (id)
);

CREATE INDEX ix_forks_fork_id ON forks (fork_id);

CREATE INDEX ix_forks_original_id ON forks (original_id);

CREATE TABLE published_pack (
    id INTEGER NOT NULL, 
    user_id INTEGER NOT NULL, 
    rating FLOAT, 
    subject VARCHAR(30) NOT NULL, 
    university VARCHAR(30) NOT NULL, 
    professor VARCHAR(60) NOT NULL, 
    course_book VARCHAR(80) NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(id) REFERENCES packs (id), 
    FOREIGN KEY(user_id) REFERENCES users (id)
);

CREATE INDEX ix_published_pack_course_book ON published_pack (course_book);

CREATE INDEX ix_published_pack_professor ON published_pack (professor);

CREATE INDEX ix_published_pack_subject ON published_pack (subject);

CREATE INDEX ix_published_pack_university ON published_pack (university);

CREATE TABLE tokens (
    id SERIAL NOT NULL, 
    user_id INTEGER NOT NULL, 
    refresh_token_hash TEXT NOT NULL, 
    is_revoked BOOLEAN NOT NULL, 
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(user_id) REFERENCES users (id)
);

CREATE INDEX ix_tokens_id ON tokens (id);

CREATE INDEX ix_tokens_user_id ON tokens (user_id);

CREATE TABLE card_options (
    id SERIAL NOT NULL, 
    content TEXT NOT NULL, 
    is_right BOOLEAN NOT NULL, 
    card_id INTEGER NOT NULL, 
    PRIMARY KEY (id), 
    FOREIGN KEY(card_id) REFERENCES cards (id)
);

CREATE INDEX ix_card_options_id ON card_options (id);

CREATE TABLE replies (
    initial_id INTEGER NOT NULL, 
    reply_id INTEGER NOT NULL, 
    PRIMARY KEY (initial_id, reply_id), 
    FOREIGN KEY(initial_id) REFERENCES comments (id), 
    FOREIGN KEY(reply_id) REFERENCES comments (id)
);

CREATE INDEX ix_replies_initial_id ON replies (initial_id);

CREATE INDEX ix_replies_reply_id ON replies (reply_id);

UPDATE alembic_version SET version_num='fe2ebf3f76c2' WHERE alembic_version.version_num = '471509bc311b';

COMMIT;
