CREATE TABLE folo_user (
    user_id bigint primary key,
    name varchar(255),
    tag varchar(255),
    main_id bigint,
    anchor boolean DEFAULT false NOT NULL
);

CREATE TABLE folo_pidor (
    chat_id bigint NOT NULL,
    user_id bigint NOT NULL,
    score integer,
    last_win_date date,
    last_active_date date DEFAULT now() NOT NULL,
    messages_per_day integer DEFAULT 0 NOT NULL,

    primary key (chat_id, user_id),
    foreign key (user_id) references folo_user (user_id)
);

CREATE TABLE folo_var (
    chat_id bigint NOT NULL,
    type varchar(255) NOT NULL,
    value varchar(255),

    primary key (chat_id, type)
);
