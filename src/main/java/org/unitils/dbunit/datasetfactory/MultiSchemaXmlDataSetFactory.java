package org.unitils.dbunit.datasetfactory;

import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.unitils.dbunit.util.DataSetXmlReader;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import org.unitils.core.UnitilsException;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    private String defaultSchemaName;

    public void init(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

    public MultiSchemaDataSet createDataSet(String dataSetFileName, InputStream dataSetFileContents) {
        try {
            DataSetXmlReader dataSetXmlReader = new DataSetXmlReader(defaultSchemaName);
            return dataSetXmlReader.readDataSetXml(dataSetFileContents);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for input stream.", e);
        } finally {
            closeQuietly(dataSetFileContents);
        }
    }

    public String getDataSetFileExtension() {
        return "xml";
    }
}
