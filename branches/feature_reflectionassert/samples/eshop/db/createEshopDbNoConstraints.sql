create schema ESHOP authorization DBA;

create table ESHOP.USER (id bigint, userName varchar(255), age integer, primary key (id));
create table ESHOP.PRODUCT (id bigint, name varchar(255), minimumAge integer, primary key (id));
create table ESHOP.PRODUCT_PRICE_TABLE (id bigint, amount integer, price double, PRODUCT_ID bigint, primary key (id));
create table ESHOP.PURCHASE (id bigint, USER_ID bigint, primary key (id));
create table ESHOP.PURCHASE_ITEM (id bigint, amount integer, PURCHASE_ID bigint, PRODUCT_ID bigint, primary key (id));

create sequence ESHOP.PRICE_TABLE_ITEM_SEQ start with 1;
create sequence ESHOP.PRODUCT_ID_SEQ start with 1;
create sequence ESHOP.PURCHASE_ID_SEQ start with 1;
create sequence ESHOP.PURCHASE_ITEM_ID_SEQ start with 1;
create sequence ESHOP.USER_ID_SEQ start with 1;
