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
package org.unitils.dbmaintainer.dtd;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

/**
 * Implementation of {@link DtdGenerator} for the DbUnit {@link FlatXmlDataSet} XML test data files format
 * <p/>
 * todo test and fix for hsqldb (see sample project)
 */
public class FlatXmlDataSetDtdGenerator extends DatabaseTask implements DtdGenerator {

    /* Property key of the filename of the generated DTD  */
    private static final String PROPKEY_DTD_FILENAME = "dtdGenerator.dtd.filename";

    /* The DTD file name */
    private String dtdFileName;

    protected void doInit(Configuration configuration) {
        dtdFileName = configuration.getString(PROPKEY_DTD_FILENAME);
    }

    /**
     * Generates the DTD, and writes it to the file specified by the property {@link #PROPKEY_DTD_FILENAME}
     *
     * @see org.unitils.dbmaintainer.dtd.DtdGenerator#generateDtd()
     */
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
            throw new UnitilsException("Error generating DTD file", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
}
