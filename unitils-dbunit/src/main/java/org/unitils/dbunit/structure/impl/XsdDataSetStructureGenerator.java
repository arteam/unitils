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
package org.unitils.dbunit.structure.impl;

import org.dbmaintain.database.Database;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.structure.DataSetStructureGenerator;
import org.unitils.util.PropertyUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Implementation of {@link DataSetStructureGenerator} that generates xml schema files for data sets.
 * <p/>
 * This will generate an xsd for each configured database schema. Each database schema will be described in an xsd named
 * 'schema_name'.xsd. A general dataset.xsd will also be generated. This xsd refers to the database schema specific xsds.
 *
 * todo filter dbmaintain_scripts table
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGenerator implements DataSetStructureGenerator {

    /* Property key for the target directory for the generated xsd files */
    public static final String PROPKEY_XSD_DIR_NAME = "dataSetStructureGenerator.xsd.dirName";

    /* Property key for the suffix to use when defining complex types for the table definitions */
    public static final String PROPKEY_XSD_COMPLEX_TYPE_SUFFIX = "dataSetStructureGenerator.xsd.complexTypeSuffix";

    /* The target directory for the xsd files */
    private String xsdDirectoryName;

    /* The suffix to use when defining complex types for the table definitions */
    private String complexTypeSuffix;

    private Database defaultDatabase;


    /**
     * Initializes the generator.
     *
     * @param configuration   The config, not null
     * @param defaultDatabase The database instance, not null
     */
    public void init(Properties configuration, Database defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
        this.xsdDirectoryName = PropertyUtils.getString(PROPKEY_XSD_DIR_NAME, configuration);
        this.complexTypeSuffix = PropertyUtils.getString(PROPKEY_XSD_COMPLEX_TYPE_SUFFIX, configuration);
    }


    /**
     * Generates the XSDs, and writes them to the target directory specified by the property {@link #PROPKEY_XSD_DIR_NAME}.
     */
    public void generateDataSetStructure() {
        File xsdDirectory = new File(xsdDirectoryName);
        xsdDirectory.mkdirs();

        generateDataSetXsd(xsdDirectory);
        for (String schemaName : defaultDatabase.getSchemaNames()) {
            generateDatabaseSchemaXsd(schemaName, xsdDirectory);
        }
    }


    /**
     * Generates a general dataset xsd that will refer to database schema specific dataset XSDs.
     *
     * @param xsdDirectory The target directory, not null
     */
    protected void generateDataSetXsd(File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, "dataset.xsd")));

            String defaultSchemaName = defaultDatabase.getDefaultSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns:dflt=\"" + defaultSchemaName + "\">\n");

            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write("\t<xsd:import namespace=\"" + schemaName + "\" schemaLocation=\"" + schemaName + ".xsd\" />\n");
            }

            writer.write("\t<xsd:element name=\"dataset\">\n");
            writer.write("\t\t<xsd:complexType>\n");
            writer.write("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");

            Set<String> defaultSchemaTableNames = defaultDatabase.getTableNames();
            for (String tableName : defaultSchemaTableNames) {
                writer.write("\t\t\t\t<xsd:element name=\"" + tableName + "\" type=\"dflt:" + tableName + complexTypeSuffix + "\" />\n");
            }
            writer.write("\t\t\t\t<xsd:any namespace=\"" + defaultSchemaName + "\" />\n");

            writer.write("\t\t\t</xsd:choice>\n");
            writer.write("\t\t</xsd:complexType>\n");
            writer.write("\t</xsd:element>\n");
            writer.write("</xsd:schema>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating xsd file: " + xsdDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }


    /**
     * Generates an XSD for the database schema of the given db support.
     *
     * @param schemaName   The db support, not null
     * @param xsdDirectory The target directory, not null
     */
    protected void generateDatabaseSchemaXsd(String schemaName, File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, schemaName + ".xsd")));

            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns=\"" + schemaName + "\" targetNamespace=\"" + schemaName + "\">\n");

            Set<String> tableNames = defaultDatabase.getTableNames(schemaName);
            for (String tableName : tableNames) {
                writer.write("\t<xsd:element name=\"" + tableName + "\" type=\"" + tableName + complexTypeSuffix + "\" />\n");
            }

            for (String tableName : tableNames) {
                writer.write("\t<xsd:complexType name=\"" + tableName + complexTypeSuffix + "\">\n");

                Set<String> columnNames = defaultDatabase.getColumnNames(schemaName, tableName);
                for (String columnName : columnNames) {
                    writer.write("\t\t<xsd:attribute name=\"" + columnName + "\" use=\"optional\" />\n");
                }
                writer.write("\t</xsd:complexType>\n");
            }
            writer.write("</xsd:schema>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating xsd file: " + xsdDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }

}
