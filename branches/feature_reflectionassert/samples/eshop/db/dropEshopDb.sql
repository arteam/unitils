drop table ESHOP.PRODUCT if exists cascade;
drop table ESHOP.PRODUCT_PRICE_TABLE if exists cascade;
drop table ESHOP.PURCHASE if exists cascade;
drop table ESHOP.PURCHASE_ITEM if exists cascade;
drop table ESHOP.USER if exists cascade;
drop table ESHOP.DB_VERSION if exists cascade;

drop sequence ESHOP.PRICE_TABLE_ITEM_SEQ if exists;
drop sequence ESHOP.PRODUCT_ID_SEQ if exists;
drop sequence ESHOP.PURCHASE_ID_SEQ if exists;
drop sequence ESHOP.PURCHASE_ITEM_ID_SEQ if exists;
drop sequence ESHOP.USER_ID_SEQ if exists;
