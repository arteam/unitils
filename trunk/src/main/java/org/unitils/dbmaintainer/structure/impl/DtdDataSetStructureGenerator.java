/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dbmaintainer.structure.impl;

import org.apache.commons.lang.StringUtils;
import org.dbmaintain.dbsupport.DbSupport;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.write;
import org.unitils.util.PropertyUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

/**
 * Implementation of {@link DataSetStructureGenerator} for the DbUnit <code>FlatXmlDataSet</code> XML test data files format
 * <p/>
 * todo test and fix for hsqldb (see sample project)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DtdDataSetStructureGenerator implements DataSetStructureGenerator {

    /* Property key of the filename of the generated DTD  */
    public static final String PROPKEY_DTD_FILENAME = "dtdGenerator.dtd.filename";

    /* The DTD file name */
    private String dtdFileName;

    private DbSupport defaultDbSupport;

    /**
     * Initializes the generator by retrieving the name for the DTD file.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        dtdFileName = PropertyUtils.getString(PROPKEY_DTD_FILENAME, configuration);
        defaultDbSupport = getDatabaseModule().getDbMaintainFacade().getDefaultDbSupport();
    }


    /**
     * Generates the DTD, and writes it to the file specified by the property {@link #PROPKEY_DTD_FILENAME}.
     * The DTD will contain the structure of the database. All tables will be written as optional elements and
     * all columns will be optional attributes.
     */
    public void generateDataSetStructure() {
        Writer writer = null;
        try {
            // creates the DTD file
            File dtdFile = new File(dtdFileName);
            File parentDirectory = dtdFile.getParentFile();
            if (parentDirectory == null) {
                throw new UnitilsException("Error generating DTD file. Could not find parent directory for DTD file: " + dtdFileName);
            }
            parentDirectory.mkdirs();

            // generate content as a string
            String dtdContent = generateDtdContent();
            // make all elements optional
            dtdContent = StringUtils.replace(dtdContent, "#REQUIRED\n", "#IMPLIED\n");

            // write the content to the file
            writer = new FileWriter(dtdFile);
            write(dtdContent, writer);

        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Error generating DTD file: " + dtdFileName, e);
        } finally {
            closeQuietly(writer);
        }
    }


    /**
     * Generates the actual content of the DTD file as an in-memory string.
     *
     * @return the DTD content, not null
     */
    protected String generateDtdContent() {
        Connection conn = null;
        try {
            conn = defaultDbSupport.getDataSource().getConnection();
            IDatabaseConnection dbUnitDatabaseConnection = new DatabaseConnection(conn, defaultDbSupport.getDefaultSchemaName());

            StringWriter stringWriter = new StringWriter();

            FlatDtdWriter datasetWriter = new FlatDtdWriter(stringWriter);
            datasetWriter.setContentModel(FlatDtdWriter.CHOICE);

            // create a dataset for the database content
            // filter out all system table names
            Set<String> tableNames = defaultDbSupport.getTableNames(defaultDbSupport.getDefaultSchemaName());
            IDataSet actualDataSet = dbUnitDatabaseConnection.createDataSet();
            IDataSet filteredActualDataSet = new FilteredDataSet(new IncludeTableFilter(tableNames.toArray(new String[0])), actualDataSet);

            datasetWriter.write(filteredActualDataSet);
            return stringWriter.toString();

        } catch (Exception e) {
            throw new UnitilsException("Error generating content for DTD file.", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }
}
