package be.ordina.unitils.db.ant;

import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.tools.ant.BuildException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

/**
 * Ant task that generates a DTD from the unit test database.
 */
public class DBUnitDTDTask extends BaseUnitilsTask {

    private static final String PROPKEY_DTD_NAME = "database.dbunit.dtd";

    private String dtdFileName;

    public void doExecute() throws BuildException {
        dtdFileName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DTD_NAME);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            IDatabaseConnection dbUnitConn = new DatabaseConnection(conn, schemaName.toUpperCase());
            // write DTD file
            File dtdFile = new File(dtdFileName);
            dtdFile.getParentFile().mkdirs();
            FlatDtdDataSet.write(dbUnitConn.createDataSet(), new FileOutputStream(dtdFile));
        } catch (Exception e) {
            throw new BuildException("Error generating DTD file", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

}
