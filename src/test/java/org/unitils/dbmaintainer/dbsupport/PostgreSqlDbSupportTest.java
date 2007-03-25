package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import static java.util.Arrays.asList;
import java.util.Properties;
import java.util.Set;

/**
 * Tests for the PostgreSql database support.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PostgreSqlDbSupportTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PostgreSqlDbSupportTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* Instance under test */
    private PostgreSqlDbSupport postgreSqlDbSupport;

    /* The sql statement handler */
    private StatementHandler statementHandler;

    /* True if PostgreSql is not the current dialect */
    private boolean disabled;


    /**
     * Sets up the test fixture.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.postgreSqlDbSupport = new PostgreSqlDbSupport();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        disabled = !"postgresql".equals(PropertyUtils.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            logger.warn("PostgreSql is not current dialect. Skipping all tests.");
            return;
        }

        statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
        postgreSqlDbSupport.init(dataSource, "public", statementHandler);

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (disabled) {
            return;
        }
        cleanupTestDatabase();
    }


    /**
     * Tests getting the sequence names.
     */
    public void testGetSequenceNames() throws Exception {
        if (disabled) {
            return;
        }
        Set<String> result = postgreSqlDbSupport.getSequenceNames();
        assertLenEquals(asList("test_sequence", "Test_CASE_Sequence"), result);
    }


    /**
     * Tests getting the trigger names.
     */
    public void testGetTriggerNames() throws Exception {
        if (disabled) {
            return;
        }
        Set<String> result = postgreSqlDbSupport.getTriggerNames();
        assertLenEquals(asList("test_trigger", "Test_CASE_Trigger"), result);
    }


    /**
     * Tests getting the trigger names but no triggers in db.
     */
    public void testGetTriggerNames_noFound() throws Exception {
        if (disabled) {
            return;
        }
        cleanupTestDatabase();
        Set<String> result = postgreSqlDbSupport.getTriggerNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the user-defined type names.
     */
    public void testGetTypeNames() throws Exception {
        if (disabled) {
            return;
        }
        Set<String> result = postgreSqlDbSupport.getTypeNames();
        assertLenEquals(asList("test_type", "Test_CASE_Type"), result);
    }


    /**
     * Tests getting the user-defined types names but no user-defined types in db.
     */
    public void testGetTypeNames_noFound() throws Exception {
        if (disabled) {
            return;
        }
        cleanupTestDatabase();
        Set<String> result = postgreSqlDbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a type.
     */
    public void testDropType() throws Exception {
        if (disabled) {
            return;
        }

        Set<String> typeNames = postgreSqlDbSupport.getTypeNames();
        for (String typeName : typeNames) {
            postgreSqlDbSupport.dropType(typeName);
        }
        Set<String> result = postgreSqlDbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests incrementing and getting the current sequence value.
     */
    public void testGetCurrentValueOfSequence() throws Exception {
        if (disabled) {
            return;
        }

        postgreSqlDbSupport.incrementSequenceToValue("test_sequence", 30);
        long result = postgreSqlDbSupport.getCurrentValueOfSequence("test_sequence");
        assertEquals(30, result);
    }


    /**
     * Tests removing a not null constraint from the test table.
     */
    public void testRemoveNotNullConstraint() throws Exception {
        if (disabled) {
            return;
        }

        postgreSqlDbSupport.removeNotNullConstraint("test_table", "col2");
        statementHandler.handle("insert into test_table (col1, col2) values ('test', NULL)");
    }


    /**
     * Tests getting all foreign key constraints for the test table.
     */
    public void testGetTableConstraintNames() throws Exception {
        if (disabled) {
            return;
        }
        Set<String> result = postgreSqlDbSupport.getTableConstraintNames("Test_CASE_Table");
        assertLenEquals(asList("Test_CASE_Table_col1_fkey"), result);
    }


    /**
     * Tests disabling a foreign key constraint on the test table.
     */
    public void testDisableConstraint() throws Exception {
        if (disabled) {
            return;
        }
        Set<String> constraintNames = postgreSqlDbSupport.getTableConstraintNames("Test_CASE_Table");
        for (String constraintName : constraintNames) {
            postgreSqlDbSupport.disableConstraint("Test_CASE_Table", constraintName);
        }
        Set<String> result = postgreSqlDbSupport.getTableConstraintNames("Test_CASE_Table");
        assertTrue(result.isEmpty());
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    protected void createTestDatabase() throws Exception {
        // create tables
        statementHandler.handle("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)");
        statementHandler.handle("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))");
        // create views
        statementHandler.handle("create view test_view as select col1 from test_table");
        statementHandler.handle("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"");
        // create sequences
        statementHandler.handle("create sequence test_sequence");
        statementHandler.handle("create sequence \"Test_CASE_Sequence\"");
        // create triggers
        try {
            statementHandler.handle("create language plpgsql");
        } catch (Exception e) {
            // ignore language already exists
        }
        statementHandler.handle("create or replace function test() returns trigger as $$ declare begin end; $$ language plpgsql");
        statementHandler.handle("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()");
        statementHandler.handle("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()");
        // create types
        statementHandler.handle("create type test_type AS (col1 int)");
        statementHandler.handle("create type \"Test_CASE_Type\" AS (col1 int)");
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected void cleanupTestDatabase() throws Exception {
        dropTestTables("test_table", "\"Test_CASE_Table\"");
        dropTestViews("test_view", "\"Test_CASE_View\"");
        dropTestSequences("test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers("test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes("test_type", "\"Test_CASE_Type\"");
    }


    /**
     * Drops the test tables
     *
     * @param tableNames The tables to drop
     */
    protected void dropTestTables(String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String correctCaseTableName = postgreSqlDbSupport.toCorrectCaseIdentifier(tableName);
                postgreSqlDbSupport.dropTable(correctCaseTableName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param viewNames The views to drop
     */
    protected void dropTestViews(String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String correctCaseViewName = postgreSqlDbSupport.toCorrectCaseIdentifier(viewName);
                postgreSqlDbSupport.dropView(correctCaseViewName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param sequenceNames The sequences to drop
     */
    protected void dropTestSequences(String... sequenceNames) {
        for (String sequenceName : sequenceNames) {
            try {
                String correctCaseSequenceName = postgreSqlDbSupport.toCorrectCaseIdentifier(sequenceName);
                postgreSqlDbSupport.dropSequence(correctCaseSequenceName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param triggerNames The triggers to drop
     */
    protected void dropTestTriggers(String... triggerNames) {
        for (String triggerName : triggerNames) {
            try {
                String correctCaseTriggerName = postgreSqlDbSupport.toCorrectCaseIdentifier(triggerName);
                postgreSqlDbSupport.dropTrigger(correctCaseTriggerName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test types
     *
     * @param typeNames The types to drop
     */
    protected void dropTestTypes(String... typeNames) {
        for (String typeName : typeNames) {
            try {
                String correctCaseTypeName = postgreSqlDbSupport.toCorrectCaseIdentifier(typeName);
                postgreSqlDbSupport.dropType(correctCaseTypeName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }
}
