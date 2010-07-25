/*
 * Copyright Unitils.org
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
package org.unitils.dataset.structure.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.Database;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.structure.DataSetStructureGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * DataSetStructureGenerator that generates xml schema files for data sets.
 * <p/>
 * This will generate an xsd for each configured database schema. Each database schema will be described in an xsd named
 * 'schema_name'.xsd. A general dataset.xsd will also be generated. The general XSD then refers to the database schema specific XDSs.
 *
 * todo filter dbmaintain_scripts table
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGenerator implements DataSetStructureGenerator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGenerator.class);

    /* The suffix to use when defining complex types for the table definitions */
    protected static final String complexTypeSuffix = "__type";

    /* The meta data for the database */
    protected DatabaseMetaData databaseMetaData;


    /**
     * @param databaseMetaData The database meta data, not null
     */
    public void init(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }


    /**
     * Generates the XSDs that describe the stucture of the database schemas.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    public void generateDataSetStructure(File targetDirectory) {
        logger.info("Creating data set xsd files in directory: " + targetDirectory);

        targetDirectory.mkdirs();

        generateDataSetXsd(targetDirectory);
        Database defaultDatabase = databaseMetaData.getDefaultDatabase();
        for (String schemaName : defaultDatabase.getSchemaNames()) {
            generateSchemaXsd(schemaName, defaultDatabase, targetDirectory);
        }
    }

    /**
     * Generates a sample template xml file that can be used as a starting point for writing a data set file.
     * This is an xml file with the correct namespace declarations for using the XSDs.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    public void generateDataSetTemplateXmlFile(File targetDirectory) {
        logger.info("Creating sample data set xml files in directory: " + targetDirectory);
        targetDirectory.mkdirs();
        generateTemplateXml(targetDirectory);
    }


    /**
     * Generates a general data set xsd that will refer to database schema specific data set XSDs.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    protected void generateDataSetXsd(File targetDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(targetDirectory, "dataset.xsd")));

            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"unitils-dataset\">\n");

            Database defaultDatabase = databaseMetaData.getDefaultDatabase();
            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write("\t<xsd:import namespace=\"" + schemaName + "\" schemaLocation=\"" + schemaName + ".xsd\"/>\n");
            }

            writer.write("\t<xsd:element name=\"dataset\">\n");
            writer.write("\t\t<xsd:complexType>\n");

            writer.write("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
            writer.write("\t\t\t\t<xsd:element name=\"notExists\" type=\"notExists__type\"/>\n");
            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write("\t\t\t\t<xsd:any namespace=\"" + schemaName + "\"/>\n");
            }
            writer.write("\t\t\t</xsd:choice>\n");

            writer.write("\t\t\t<xsd:attribute name=\"caseSensitive\" use=\"optional\" type=\"xsd:boolean\"/>\n");
            writer.write("\t\t\t<xsd:attribute name=\"literalToken\" use=\"optional\" type=\"xsd:string\"/>\n");
            writer.write("\t\t\t<xsd:attribute name=\"variableToken\" use=\"optional\" type=\"xsd:string\"/>\n");
            writer.write("\t\t</xsd:complexType>\n");
            writer.write("\t</xsd:element>\n");
            writer.write("\t<xsd:complexType name=\"notExists__type\">\n");
            writer.write("\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write("\t\t\t<xsd:any namespace=\"" + schemaName + "\"/>\n");
            }
            writer.write("\t\t</xsd:choice>\n");
            writer.write("\t</xsd:complexType>\n");
            writer.write("</xsd:schema>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating xsd file: " + targetDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }


    /**
     * Generates an XSD for the database schema of the given db support.
     *
     * @param schemaName      The name of the schema to generate an XSD for, not null
     * @param database        The db support, not null
     * @param targetDirectory The target directory, not null
     */
    protected void generateSchemaXsd(String schemaName, Database database, File targetDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(targetDirectory, schemaName + ".xsd")));
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns=\"" + schemaName + "\" targetNamespace=\"" + schemaName + "\">\n");

            Set<String> tableNames = database.getTableNames(schemaName);
            for (String tableName : tableNames) {
                writer.write("\t<xsd:element name=\"" + tableName + "\" type=\"" + tableName + complexTypeSuffix + "\" />\n");
            }

            for (String tableName : tableNames) {
                writer.write("\t<xsd:complexType name=\"" + tableName + complexTypeSuffix + "\">\n");
                writer.write("\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
                writer.write("\t\t\t<xsd:any namespace=\"" + schemaName + "\"/>\n");
                writer.write("\t\t</xsd:choice>\n");

                Set<String> columnNames = database.getColumnNames(schemaName, tableName);
                for (String columnName : columnNames) {
                    writer.write("\t\t<xsd:attribute name=\"" + columnName + "\" use=\"optional\" />\n");
                }
                writer.write("\t</xsd:complexType>\n");
            }
            writer.write("</xsd:schema>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating xsd file: " + targetDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }


    /**
     * Generates a template xml file that uses the XSDs.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    protected void generateTemplateXml(File targetDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(targetDirectory, "dataset-template.xml")));

            String defaultSchemaName = databaseMetaData.getDefaultDatabase().getDefaultSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<uni:dataset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            writer.write("\t\t\txmlns=\"" + defaultSchemaName + "\"");

            Database defaultDatabase = databaseMetaData.getDefaultDatabase();
            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write(" xmlns:" + schemaName + "=\"" + schemaName + "\"");
            }
            writer.write(" xmlns:uni=\"unitils-dataset\"\n");
            writer.write("\t\t\txsi:schemaLocation=\"");
            for (String schemaName : defaultDatabase.getSchemaNames()) {
                writer.write(schemaName + " " + schemaName + ".xsd ");
            }
            writer.write("unitils-dataset dataset.xsd\">\n\n\n");
            writer.write("</uni:dataset>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating template xml file: " + targetDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }

}