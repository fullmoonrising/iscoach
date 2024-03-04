create table donate_session (
    id uuid primary key,
    date_time timestamp with time zone default now() not null
);

comment on table donate_session is 'Сессии за донат';