
-- a function declaration
create
or   replace function funtion1(arg in number) return varchar2 DETERMINISTIC is
  test_return_value varchar2(1000);
begin
   select 'test'''
   into lv_return_value
   from dual;

   return lv_return_value;

exception
   when no_data_found then
       return '';
end;
/


create -- another function declaration
function funtion2(arg in number) return varchar2 DETERMINISTIC is
  test_return_value varchar2(1000);
begin
   /** something / in the body */
   -- ////
exception
   when no_data_found then
       return '';
end;
/


-- an anonymous block
declare
  userName varchar2(30);
begin
  select user into userName from dual;
  DBMS_UTILITY.compile_schema(userName);
end;
/


-- a regular statement
create table table1 (col1 smallint);

////

/* a regular statement ending with a slash */
create
table table1 (col1 smallint);
/

/
/