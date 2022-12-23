CREATE TABLE IF NOT EXISTS folo_web_user (
    id bigint primary key,
    username varchar(255) NOT NULL COLLATE pg_catalog."default",
    password varchar(255) NOT NULL COLLATE pg_catalog."default",
    active boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS folo_web_user_role(
    id bigint NOT NULL,
    roles character varying(255) COLLATE pg_catalog."default",
        FOREIGN KEY (id)
        REFERENCES folo_web_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

create extension if not exists pgcrypto;

insert into folo_web_user (id, username, password, active) values (1 ,'everbald', crypt('${DB_PASS}', gen_salt('bf', 8)), true);
insert into folo_web_user_role (id, roles) values (1, 'ADMIN'), (1, 'USER');