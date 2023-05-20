DROP TABLE users;

CREATE TABLE users
(
    id BIGINT PRIMARY KEY ,
    username VARCHAR(128) UNIQUE ,
    firstname VARCHAR(128),
    lastname VARCHAR(128),
    birth_date DATE,
    role VARCHAR(32),
    info JSONB
);

create sequence users_id_seq
owned by users.id;