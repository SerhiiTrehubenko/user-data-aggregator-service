create table if not exists users
(
    user_id    varchar(50) not null primary key,
    login      varchar(50) not null,
    first_name varchar(50) not null,
    last_name  varchar(50) not null
);