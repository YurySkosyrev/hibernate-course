DROP TABLE users;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY ,
    username VARCHAR(128) ,
    firstname VARCHAR(128),
    lastname VARCHAR(128),
    birth_date DATE,
    role VARCHAR(32),
    info JSONB ,
    company_id INT REFERENCES company (id)
);

DROP TABLE profile;

CREATE TABLE profile (
    id BIGSERIAL,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users (id),
    street VARCHAR(128),
    language VARCHAR(2)
);

CREATE TABLE company (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE chat (
    id SERIAL PRIMARY KEY ,
    name VARCHAR (64) NOT NULL UNIQUE
);

DROP TABLE users_chat;

CREATE TABLE users_chat (
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT REFERENCES users(id),
    chat_id BIGINT REFERENCES chat(id),
    created_at TIMESTAMP NOT NULL ,
    created_by VARCHAR(128),
    UNIQUE (user_id, chat_id)
);

create sequence users_id_seq
owned by users.id;

DROP sequence users_id_seq;

create table all_sequence
(
    table_name VARCHAR(32) PRIMARY KEY ,
    pk_value BIGINT NOT NULL
);

INSERT INTO company (name)
VALUES ('Google');

INSERT INTO users (username, firstname, lastname, birth_date, company_id)
VALUES ('petr@gmail.com', 'Petr', 'Petrov', '2000-12-22', 1),
       ('ivan@gmail.com', 'Ivan', 'Ivanov', '2001-12-22', 1);


