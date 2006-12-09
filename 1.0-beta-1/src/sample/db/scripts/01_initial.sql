create table USER (id bigint not null, userName varchar(255) not null, age integer not null, primary key (id));

create table PRODUCT (id bigint not null, name varchar(255) not null, minimumAge integer not null, primary key (id));

create table PRODUCT_PRICE_TABLE (id bigint not null, amount integer not null, price double not null, PRODUCT_ID bigint not null, primary key (id), foreign key (product_id) references product(id) );

create table PURCHASE (id bigint not null, USER_ID bigint not null, primary key (id), foreign key (user_id) references user(id) );

create table PURCHASE_ITEM (id bigint not null, amount integer not null, PURCHASE_ID bigint not null, primary key (id), foreign key (purchase_id)  references purchase(id) );
 
