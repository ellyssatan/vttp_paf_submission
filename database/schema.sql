create database eshop;

use eshop;

create table customers (
    name varchar(32) UNIQUE not null,
    address varchar(128) not null,
    email varchar(128) not null,

    primary key(name) 
);

insert into customers
    (name, address, email)
values
    ('fred', '201 Cobblestone Lane', 'fredflintstone@bedrock.com'),
    ('sherlock', '221B Baker Street, London', 'sherlock@consultingdetective.org'),
    ('spongebob', '124 Conch Street, Bikini Bottom', 'spongebob@yahoo.com'),
    ('jessica', '698 Candlewood Land, Cabot Cove', 'fletcher@gmail.com'),
    ('dursley', '4 Privet Drive, Little Whinging, Surrey', 'dursley@gmail.com');

create table orders (
    orderId varchar(8) UNIQUE not null,
	deliveryId varchar(32) UNIQUE not null,
	name varchar(32) UNIQUE not null,
	address varchar(128) not null,
    email varchar(128) not null,
	status varchar(16) not null,
	orderDate date,

    primary key(orderId),
    constraint fk_name
        foreign key(name) references customers(name)
);

create table line_item (
    item text not null,
    quantity int default '1',
    orderId char(8) not null,

    constraint fk_orderId
        foreign key(orderId) references orders(orderId)
);

create table order_status (
    order_id char(8) not null,
    delivery_id varchar(128),
    status enum('pending', 'dispatched'),
    status_update datetime,

    constraint fk_order_id
        foreign key(order_id) references orders(orderId)
);
