/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.dtd;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.unitils.core.UnitilsException;

/**
 * Implementation of {@link DtdGenerator} for the DbUnit {@link FlatXmlDataSet} XML test data files format
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

    /**
     * Initializes the connection to the database, and the path of the file where the DTD should be written into
     * 
     * @see org.unitils.dbmaintainer.dtd.DtdGenerator#init(org.apache.commons.configuration.Configuration, javax.sql.DataSource)
     */
    public void init(Configuration configuration, DataSource dataSource) {
        this.dataSource = dataSource;

        dtdFileName = configuration.getString(PROPKEY_DTD_FILENAME);
        schemaName = configuration.getString(PROPKEY_SCHEMA_NAME);
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
