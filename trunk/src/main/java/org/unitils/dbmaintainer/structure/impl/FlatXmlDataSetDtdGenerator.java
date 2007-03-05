/*
 * Copyright 2006 the original author or authors.
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

import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.*;
import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.structure.DtdGenerator;
import org.unitils.util.PropertyUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Properties;

/**
 * Implementation of {@link DtdGenerator} for the DbUnit {@link FlatXmlDataSet} XML test data files format
 * <p/>
 * todo test and fix for hsqldb (see sample project)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FlatXmlDataSetDtdGenerator extends DatabaseTask implements DtdGenerator {

    /* Property key of the filename of the generated DTD  */
    public static final String PROPKEY_DTD_FILENAME = "dtdGenerator.dtd.filename";

    /* The DTD file name */
    private String dtdFileName;


    /**
     * Initializes the generator by retrieving the name for the DTD file.
     *
     * @param configuration The config, not null
     */
    protected void doInit(Properties configuration) {
        dtdFileName = PropertyUtils.getString(PROPKEY_DTD_FILENAME, configuration);
    }


    /**
     * Generates the DTD, and writes it to the file specified by the property {@link #PROPKEY_DTD_FILENAME}.
     * The DTD will contain the structure of the dataabase. All tables will be written as optional elements and
     * all columns will be optional attributes.
     *
     * @see DtdGenerator#generateDtd()
     */
    public void generateDtd() {
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
            conn = dataSource.getConnection();
            IDatabaseConnection dbUnitConn = new DatabaseConnection(conn, schemaName);

            StringWriter stringWriter = new StringWriter();

            FlatDtdWriter datasetWriter = new FlatDtdWriter(stringWriter);
            datasetWriter.setContentModel(FlatDtdWriter.CHOICE);
            datasetWriter.write(dbUnitConn.createDataSet());
            return stringWriter.toString();

        } catch (Exception e) {
            throw new UnitilsException("Error generating DTD file", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
}
