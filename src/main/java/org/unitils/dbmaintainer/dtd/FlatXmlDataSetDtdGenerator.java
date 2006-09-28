package org.unitils.dbmaintainer.dtd;

import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

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

    public void init(DataSource dataSource) {
        this.dataSource = dataSource;

        Configuration configuration = UnitilsConfiguration.getInstance();
        dtdFileName = configuration.getString(PROPKEY_DTD_FILENAME);
        schemaName = configuration.getString(PROPKEY_SCHEMA_NAME);
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
