create sequence users_id_seq
    owned by users.id;

DROP sequence users_id_seq;

create table all_sequence
(
    table_name VARCHAR(32) PRIMARY KEY,
    pk_value   BIGINT NOT NULL
);