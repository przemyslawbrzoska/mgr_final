create table if not exists REQUESTS
(
    ID               numeric(10, 0) not null primary key ,
    READ_DATE        timestamp      not null,
    RPI_IDENTIFIER   varchar(100)   not null,
    MOBILE_CLIENT_ID VARCHAR(100)
);

create sequence if not exists REQ_SEQ
    START WITH 1 increment by 1;

create table if not exists RASPBERRIES
(
    ID             numeric(10, 0) not null primary key ,
    CREATION_DATE  timestamp,
    RPI_IDENTIFIER varchar(100)   not null,
    RPI_POS_X      numeric(10, 2),
    RPI_POS_Y      numeric(10, 2)
);

create sequence if not exists RPI_SEQ
    START WITH 1000 increment by 1;

create table if not exists POSITIONS
(
    ID               numeric(10, 0) not null primary key ,
    CREATION_DATE    timestamp      not null,
    POSITION_POS_X   numeric(10, 2) not null,
    POSITION_POS_Y   numeric(10, 2) not null,
    MOBILE_CLIENT_ID VARCHAR(100)   not null,
    POSITION_ZONES   VARCHAR(1000)
);

create sequence if not exists POS_SEQ
    START WITH 1000 increment by 1;

create table if not exists TILES
(
    ID    numeric(10, 0) not null primary key ,
    POS_X numeric(10, 2) not null,
    POS_Y numeric(10, 2) not null
);

create sequence if not exists TILES_SEQ
    START WITH 1000 increment by 1;

create table if not exists TILES_COVERED
(
    rpi_data_id  numeric(10, 0) not null references RASPBERRIES(id),
    tile_data_id numeric(10, 0) not null references TILES(id)
);

create table if not exists ZONES
(
    ID              numeric(10, 0) not null primary key ,
    ZONE_IDENTIFIER varchar(100)   not null,
    ZONE_POS_X      numeric(5, 2),
    ZONE_POS_Y      numeric(5, 2),
    ZONE_HEIGHT     numeric(5, 2),
    ZONE_WIDTH      numeric(5, 2),
    ZONE_OCCURENCES numeric(10, 2) default 0,
    ZONE_TYPE       varchar(10)
);

create sequence if not exists ZONE_SEQ
    START WITH 1000 increment by 1;

create table if not exists FILES
(
    ID        numeric(10, 0) not null primary key ,
    FILE_DATA bytea,
    FILE_TYPE varchar(100),
    FILE_NAME varchar(100)
);

create sequence if not exists FILE_SEQ
    START WITH 1000 increment by 1;
