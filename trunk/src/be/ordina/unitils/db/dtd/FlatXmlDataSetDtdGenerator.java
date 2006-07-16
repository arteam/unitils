package be.ordina.unitils.db.dtd;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.util.Properties;
import java.sql.Connection;
import java.io.File;
import java.io.FileOutputStream;

import be.ordina.unitils.util.PropertiesUtils;

/**
 * @author Filip Neven
 */
public class FlatXmlDataSetDtdGenerator implements DtdGenerator {

    /* Property key of the filename of the generated DTD  */
    private static final String PROPKEY_DTD_FILENAME = "dtdGenerator.dtd.filename";

    /* Property key of the name of the database schema */
    private static final String PROPKEY_SCHEMA_NAME = "dataSource.userName";

    /* The datasource */
    private DataSource dataSource;

    /* The database schema name*/
    private String schemaName;

    /* The DTD file name */
    private String dtdFileName;

    public void init(Properties properties, DataSource dataSource) {
        this.dataSource = dataSource;
        dtdFileName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DTD_FILENAME);
        schemaName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCHEMA_NAME);
    }

    public void generateDtd() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            IDatabaseConnection dbUnitConn = new DatabaseConnection(conn, schemaName.toUpperCase());
            // write DTD file
            File dtdFile = new File(dtdFileName);
            dtdFile.getParentFile().mkdirs();
            FlatDtdDataSet.write(dbUnitConn.createDataSet(), new FileOutputStream(dtdFile));
        } catch (Exception e) {
            throw new RuntimeException("Error generating DTD file", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
}
