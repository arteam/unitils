/*
 * Copyright 2014 willemijnwouters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitils.dbunit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.sqlassert.SqlAssert;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;

/**
 *
 * @author willemijnwouters
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmptyTablesTest2 {
    @TestDataSource("database1")
    private DataSource dataSource;


    @BeforeClass
    public static void beforeClass() throws FileNotFoundException, IOException {
        Properties config = getCorrectProperties();
        
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.init(config);
        databaseModule.afterInit();
        DbUnitModule dbunitModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
        dbunitModule.init(config);
        dbunitModule.afterInit();
    }

    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsMoreAttributes.xml")
    public void testFirstContainsMoreAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 2l);
        SqlAssert.assertMultipleRowSqlResult("select * from person", dataSource, new String[]{"2", "Myrthe"}, new String[]{"1", null});
    }

    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsLessAttributes.xml")
    public void testFirstContainsLessAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 2l);
        SqlAssert.assertSingleRowSqlResult("select personname from person where personid='1'", dataSource, new String[]{"Willemijn"});
        //SqlAssert.assertMultipleRowSqlResult("select * from person", new String[]{"2"}, new String[]{"1", "Willemijn"});
    }

    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsSameAttributes.xml")
    public void testFirstContainsSameAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 2l);
        SqlAssert.assertMultipleRowSqlResult("select * from person", dataSource, new String[]{"3", "Maurits"}, new String[]{"4", "Franciscus"});
    }

    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsNoAttributes.xml")
    public void testFirstContainsNoAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 0L);
    }

    @Test
    @DataSet({"DbunitDifferentCollumnsTest-WithAllColumns.xml", "DbunitDifferentCollumnsTest-WithOnlyOneColumn.xml"})
    public void testFirstDataSetContainsMoreAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 4L);
        String[][] expected = new String[][]{
            new String[]{"12", "Peter"},
            new String[]{"13", "Stijn"},
            new String[]{"14", null},
            new String[]{"15", null}
        };
        
       SqlAssert.assertMultipleRowSqlResult("select * from person", expected);
    }
    
    @Test
    @DataSet({"DbunitDifferentCollumnsTest-WithOnlyOneColumn.xml", "DbunitDifferentCollumnsTest-WithAllColumns.xml"})
    public void testFirstDataSetContainsLessAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 4L);
        String[][] expected = new String[][]{
            new String[]{"12", "Peter"},
            new String[]{"13", "Stijn"},
            new String[]{"14", null},
            new String[]{"15", null}
        };
        
       SqlAssert.assertMultipleRowSqlResult("select * from person", expected);
    }
    
    @Test
    @DataSet({"DbunitDifferentCollumnsTest-WithOnlyOneColumn.xml", "DbunitDifferentCollumnsTest-WithOnlyPersonName.xml"})
    public void testFirstDataSetContainsDifferentColumnsThanSecond() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 4L);
        String[][] expected = new String[][]{
            new String[]{null, "Suzan"},
            new String[]{null, "Mathias"},
            new String[]{"14", null},
            new String[]{"15", null}
        };
        
       SqlAssert.assertMultipleRowSqlResult("select * from person", expected);
    }
    
    @Test
    @DataSets({
        
        @DataSet("DbunitDifferentCollumnsTest-WithOnlyOneColumn.xml"),
        @DataSet("DbunitDifferentCollumnsTest-WithOnlyPersonName.xml")
    })
    public void testFirstDataSetContainsDifferentColumnsThanSecondWithDataSetsAnnotation() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 4L);
        String[][] expected = new String[][]{
            new String[]{null, "Suzan"},
            new String[]{null, "Mathias"},
            new String[]{"14", null},
            new String[]{"15", null}
        };
        
       SqlAssert.assertMultipleRowSqlResult("select * from person", expected);
    }
    
    @Test
    @DataSets({
        
        @DataSet("DbunitDifferentCollumnsTest-WithOnlyOneColumn.xml"),
        @DataSet("DbunitDifferentCollumnsTest-WithAllColumns.xml")
    })
    public void testFirstDataSetContainsLessAttributesWithDataSetsAnnotation() {
        SqlAssert.assertCountSqlResult("select count(*) from person", dataSource, 4L);
        String[][] expected = new String[][]{
            new String[]{"12", "Peter"},
            new String[]{"13", "Stijn"},
            new String[]{"14", null},
            new String[]{"15", null}
        };
        
       SqlAssert.assertMultipleRowSqlResult("select * from person", expected);
    }



    private static Properties getCorrectProperties() {
        Properties config = (Properties) Unitils.getInstance().getConfiguration().clone();
        config.setProperty("database.names", "database1, database2");
        config.setProperty("database.userName", "sa");
        config.setProperty("database.password", "");
        config.setProperty("database.schemaNames", "public");
        config.setProperty("database.driverClassName.database1", "org.hsqldb.jdbcDriver"); 
        config.setProperty("database.driverClassName.database2", "org.h2.Driver");
        config.setProperty("database.url.database1", "jdbc:hsqldb:mem:unitils1");
        config.setProperty("database.url.database2", "jdbc:h2:~/test");
        config.setProperty("database.dialect.database1", "hsqldb");
        config.setProperty("database.dialect.database2", "h2");
        config.setProperty("database.dbMaintain.enabled", "true");
        config.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "true");
        config.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "false");
        config.setProperty("updateDataBaseSchema.enabled", "true");

        config.setProperty("dbMaintainer.updateSequences.enabled", "true");
        config.setProperty("dbMaintainer.keepRetryingAfterError.enabled","true");
        config.setProperty("org.unitils.dbmaintainer.script.ScriptSource.implClassName", "org.unitils.dbmaintainer.script.impl.DefaultScriptSource");
        config.setProperty("unitils.module.hibernate.enabled", "false");
        config.setProperty("unitils.module.jpa.enabled", "false");
        config.setProperty("unitils.module.spring.enabled", "false");
        config.setProperty("dbMaintainer.script.locations", "src/test/resources/dbscripts");
        config.setProperty("dbMaintainer.fromScratch.enabled", "false");
        return config;
    }
}
