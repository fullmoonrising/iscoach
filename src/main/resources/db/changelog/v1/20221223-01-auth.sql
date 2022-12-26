create table if not exists folo_web_user (
    username varchar(255) primary key,
    password varchar(255) not null,
    active boolean not null
);

create table if not exists folo_web_user_role(
    username varchar(255) not null,
    roles character varying(255),
        foreign key (username)
        references folo_web_user (username) match simple
        on update no action
        on delete no action
);

create extension if not exists pgcrypto;

insert into folo_web_user (username, password, active) values ('moonmoon', crypt('${DB_PASS}', gen_salt('bf', 8)), true);
insert into folo_web_user_role (username, roles) values ('moonmoon', 'ADMIN'), ('moonmoon', 'USER');