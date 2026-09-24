create table if not exists cities    (id bigserial primary key, name varchar(64) not null unique);
create table if not exists countries (id bigserial primary key, name varchar(64) not null unique);
create table if not exists clients (
    id               bigserial primary key,
    surname          varchar(128) not null,
    name             varchar(128) not null,
    patronymic       varchar(128) not null,
    birth_date       date         not null,
    passport_series  varchar(5)   not null,
    passport_number  varchar(6)   not null,
    city_id          bigint       not null references cities(id),
    address          varchar(255) not null,
    mobile_phone     varchar(20),
    email            varchar(128),
    employed         boolean,
    position         varchar(128),
    citizenship_id   bigint       not null references countries(id),
    military_liable  boolean      not null,
    unique (passport_series, passport_number)
);