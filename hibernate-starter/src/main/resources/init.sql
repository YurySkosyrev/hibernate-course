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

CREATE TABLE company (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(64) NOT NULL UNIQUE
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


