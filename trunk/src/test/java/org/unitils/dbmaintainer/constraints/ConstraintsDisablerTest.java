package org.unitils.dbmaintainer.constraints;

import org.unitils.UnitilsJUnit3;
import org.unitils.core.Unitils;
import org.unitils.util.ReflectionUtils;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.dbunit.DatabaseTest;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 */
@DatabaseTest
public class ConstraintsDisablerTest extends UnitilsJUnit3 {

    private ConstraintsDisabler constraintsDisabler;

    private DataSource dataSource;

    @AfterCreateDataSource
    protected void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void setUp() throws Exception {
        super.setUp();

        Configuration config = UnitilsConfiguration.getInstance();

        StatementHandler st = new JDBCStatementHandler();
        st.init(dataSource);

        constraintsDisabler = ReflectionUtils.createInstanceOfType(config.getString(DBMaintainer.PROPKEY_CONSTRAINTSDISABLER_START + '.' +
                config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT)));
        constraintsDisabler.init(config, dataSource, st);
        constraintsDisabler.init(config, dataSource, st);

        createTables();
    }

    protected void tearDown() throws Exception {
        dropTables();

        super.tearDown();
    }

    private void createTables() throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        st.execute("create table table1 (col1 varchar(10) primary key, col2 varchar(12) not null)");
        st.execute("create table table2 (col1 varchar(10), foreign key (col1) references table1(col1))");
        DbUtils.closeQuietly(conn, st, null);
    }

    private void dropTables() throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        st.execute("drop table table2 cascade");
        st.execute("drop table table1 cascade");
    }

    public void testDisableConstraints_foreignKey() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            insertForeignKeyViolation(conn);
            fail("SQLException should have been thrown");
        } catch (SQLException e) {
            // Foreign key violation, should throw SQLException
        }

        constraintsDisabler.disableConstraints();
        Connection conn = dataSource.getConnection();
        constraintsDisabler.disableConstraintsOnConnection(conn);
        // Should not throw exception anymore
        insertForeignKeyViolation(conn);
    }

    private void insertForeignKeyViolation(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("insert into table2 values ('test')");
    }

    public void testDisableConstraints_notNull() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            insertNotNullViolation(conn);
            fail("SQLException should have been thrown");
        } catch (SQLException e) {
            // Foreign key violation, should throw SQLException
        }

        constraintsDisabler.disableConstraints();
        Connection conn = dataSource.getConnection();
        constraintsDisabler.disableConstraintsOnConnection(conn);
        // Should not throw exception anymore
        insertNotNullViolation(conn);
    }

    private void insertNotNullViolation(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("insert into table1 values ('test', null)");
    }
}
