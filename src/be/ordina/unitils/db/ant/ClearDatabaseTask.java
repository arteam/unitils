package be.ordina.unitils.db.ant;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Ant tasks that drops all database tables in the current database
 */
public class ClearDatabaseTask extends BaseUnitilsTask {

    private static final Logger logger = Logger.getLogger(ClearDatabaseTask.class);

    public void doExecute() throws BuildException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();

            dropTables(conn, st);
            dropSequences(conn, st);
        } catch (Exception e) {
            throw new BuildException("Error generating DTD file", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void dropTables(Connection conn, Statement st) throws SQLException {
        ResultSet rset = null;
        try {
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, null);
            while (rset.next())  {
                String dropTableSQL = "drop table " + rset.getString("TABLE_NAME") + " cascade constraints";
                logger.info(dropTableSQL);
                st.execute(dropTableSQL);
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    private void dropSequences(Connection conn, Statement st) throws SQLException {
        ResultSet rset = null;
        try {
            rset = st.executeQuery("select SEQUENCE_NAME from USER_SEQUENCES");
            List<String> dropStatements = new ArrayList<String>();
            while (rset.next()) {
                dropStatements.add("drop sequence " + rset.getString("SEQUENCE_NAME"));
            }
            for (String dropStatement : dropStatements) {
                logger.info(dropStatement);
                st.execute(dropStatement);
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }
}
