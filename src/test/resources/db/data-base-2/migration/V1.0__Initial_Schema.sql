create table if not exists user_table
(
    ldap_id    varchar(50) not null primary key,
    ldap_login      varchar(50) not null,
    name varchar(50) not null,
    surname  varchar(50) not null
);