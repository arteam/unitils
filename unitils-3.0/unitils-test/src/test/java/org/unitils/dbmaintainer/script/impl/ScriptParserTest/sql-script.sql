
CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50));
CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20));
CREATE TABLE TABLE_B (ID INTEGER PRIMARY KEY);

CREATE
TABLE
PRODUCTS
(ID
INTEGER
PRIMARY
KEY);

INSERT INTO PERSON(NAME) VALUES ('This is
a multiline
value');

INSERT INTO PERSON(NAME) VALUES ('');
INSERT INTO "PERSON"("""NAME""") VALUES ('''');

-- comment1
CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, -- inline comment
-- comment2 /* ignored block comment*/
NAME VARCHAR2(50)); -- another comment

/* comment1 */
CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, /* inline comment */
/* comment2 -- ignored line comment */
NAME VARCHAR2(50)); /* another comment */

/* this is a
 * multiline
 * comment */
CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, /* inline comment
-- ignored line comment
*/
NAME VARCHAR2(50)); /* another
comment */

COMMENT ON TABLE PERSON IS 'This ; comment ; contains ; a semi-colon';
COMMENT ON TABLE PERSON IS 'This "comment" '' contains quotes and double quotes';

;;;;

-- last statement without ;
COMMENT ON TABLE PERSON IS 'This /* comment */ contains a block and -- line comment';

;;;