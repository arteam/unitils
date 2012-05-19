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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.unitilsnew.database.SqlAssert.assertTableCount;
import static org.unitilsnew.database.SqlUnitils.executeUpdate;
import static org.unitilsnew.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainUnitilsMarkDatabaseAsUpToDateIntegrationTest {

    private File scriptsDir;
    private File script1;


    @Before
    public void initialize() {
        scriptsDir = new File("unitils-test/src/test/resources/scripts");
        script1 = new File(scriptsDir, "01_insert.sql");

        cleanup();
        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
        executeUpdateQuietly("drop table dbmaintain_scripts");
        script1.delete();
    }


    @Test
    public void markDatabaseAsUpToDate() throws Exception {
        createScript(script1, "insert into my_table(id) values ('111');");
        DbMaintainUnitils.markDatabaseAsUpToDate();

        DbMaintainUnitils.updateDatabase();
        assertTableCount("my_table", 0);
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
