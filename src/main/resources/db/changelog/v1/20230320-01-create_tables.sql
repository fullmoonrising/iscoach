create table price_list (
    id varchar(64) primary key,
    amount integer not null
);

comment on table price_list is 'Прайслист';

create table order_info (
    id serial,
    status varchar(64),
    payment jsonb
);

comment on table order_info is 'Заказы';

create extension citext;
create domain email as citext
  check ( value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );

create table notification (
    user_id bigint primary key,
    email email
);

comment on table notification is 'Уведомления';


