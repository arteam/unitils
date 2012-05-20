/*
 * Copyright 2012,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitilsnew.database;

import org.dbmaintain.util.DbMaintainException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitilsnew.database.SqlAssert.assertTableCount;
import static org.unitilsnew.database.SqlUnitils.executeUpdate;
import static org.unitilsnew.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainUnitilsUpdateDatabaseIntegrationTest {

    private File scriptsDir;
    private File script1;
    private File script2;
    private File script3;


    @Before
    public void initialize() {
        scriptsDir = new File("unitils-test/src/test/resources/scripts");
        script1 = new File(scriptsDir, "01_insert.sql");
        script2 = new File(scriptsDir, "02_insert.sql");
        script3 = new File(scriptsDir, "03_insert.sql");

        cleanup();
        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
        executeUpdateQuietly("drop table dbmaintain_scripts");
        script1.delete();
        script2.delete();
        script3.delete();
    }


    @Test
    public void updateDatabase() throws Exception {
        DbMaintainUnitils.updateDatabase();
        assertTableCount(0, "my_table");

        createScript(script1, "insert into my_table(id) values ('111');");
        createScript(script2, "insert into my_table(id) values ('222');");
        DbMaintainUnitils.updateDatabase();
        assertTableCount(2, "my_table");

        createScript(script3, "insert into my_table(id) values ('333');");
        DbMaintainUnitils.updateDatabase();
        assertTableCount(3, "my_table");
    }

    @Test
    public void exceptionWhenOutOfSequenceScript() throws Exception {
        createScript(script2, "insert into my_table(id) values ('222');");
        DbMaintainUnitils.updateDatabase();

        createScript(script1, "insert into my_table(id) values ('111');");
        try {
            DbMaintainUnitils.updateDatabase();
            fail("DbmaintainException expected");
        } catch (DbMaintainException e) {
            assertTrue(e.getMessage().contains("Following irregular script updates were detected:"));
        }
    }

    @Test
    public void exceptionWhenScriptFails() throws Exception {
        createScript(script1, "xxx;");
        try {
            DbMaintainUnitils.updateDatabase();
            fail("DbmaintainException expected");
        } catch (DbMaintainException e) {
            assertTrue(e.getMessage().contains("Error while executing script 01_insert.sql: Unable to perform database statement:\n" +
                    "xxx"));
        }
    }

    @Test
    public void constructionForCoverage() {
        new DbMaintainUnitils();
    }


    private void createScript(File file, String content) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            PrintStream printStream = new PrintStream(fileOutputStream);
            printStream.print(content);
        } finally {
            fileOutputStream.close();
        }
    }
}
