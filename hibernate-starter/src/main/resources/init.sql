DROP TABLE company_locale;
DROP TABLE users_chat;
DROP TABLE chat;
DROP TABLE profile;
DROP TABLE users;
DROP TABLE company;

CREATE TABLE company
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(128),
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    role       VARCHAR(32),
    info       JSONB,
    company_id INT REFERENCES company (id)
);

CREATE TABLE profile
(
    id       BIGSERIAL,
    user_id  BIGINT NOT NULL UNIQUE REFERENCES users (id),
    street   VARCHAR(128),
    language VARCHAR(2)
);

CREATE TABLE chat
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE users_chat
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id),
    chat_id    BIGINT REFERENCES chat (id),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(128),
    UNIQUE (user_id, chat_id)
);

CREATE TABLE company_locale
(
    company_id  INT NOT NULL REFERENCES company (id),
    lang        VARCHAR(2),
    description VARCHAR(128) NOT NULL,
    PRIMARY KEY (company_id, lang)
);

INSERT INTO company (name)
VALUES ('Залупа');

INSERT INTO users (username, firstname, lastname, birth_date, company_id)
VALUES ('petr@gmail.com', 'Petr', 'Petrov', '2000-12-22', 1),
       ('ivan@gmail.com', 'Ivan', 'Ivanov', '2001-12-22', 1);



