create table if not exists folo_index (
    chat_id bigint not null,
    date date,
    points integer not null default 0,
    index decimal,

    primary key (chat_id, date)
);