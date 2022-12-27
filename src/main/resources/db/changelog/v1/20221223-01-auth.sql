create table if not exists users (
    username varchar(50) primary key,
    password varchar(68) not null,
    enabled boolean not null
);

create table if not exists authorities (
    username varchar(50) not null,
    authority varchar(68) not null,

    foreign key (username) references users (username)
);

create extension if not exists pgcrypto;

insert into users (username, password, enabled) values ('moonmoon', crypt('${DB_PASS}', gen_salt('bf', 8)), true);
insert into authorities (username, authority) values ('moonmoon', 'ROLE_ADMIN'), ('moonmoon', 'ROLE_USER');