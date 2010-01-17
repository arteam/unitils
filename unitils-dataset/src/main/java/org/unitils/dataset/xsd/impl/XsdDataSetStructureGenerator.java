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
package org.unitils.dataset.xsd.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import org.unitils.util.PropertyUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Implementation of {@link org.unitils.dbmaintainer.structure.DataSetStructureGenerator} that generates xml schema files for data sets.
 * <p/>
 * This will generate an xsd for each configured database schema. Each database schema will be described in an xsd named
 * 'schema_name'.xsd. A general dataset.xsd will also be generated. This xsd refers to the database schema specific xsds.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGenerator extends BaseDatabaseAccessor implements DataSetStructureGenerator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGenerator.class);

    /* Property key for the target directory for the generated xsd files */
    public static final String PROPKEY_XSD_DIR_NAME = "dataSetStructureGenerator.xsd.dirName";

    /* Property key for the suffix to use when defining complex types for the table definitions */
    public static final String PROPKEY_XSD_COMPLEX_TYPE_SUFFIX = "dataSetStructureGenerator.xsd.complexTypeSuffix";

    /* The target directory for the xsd files */
    private String xsdDirectoryName;

    /* The suffix to use when defining complex types for the table definitions */
    private String complexTypeSuffix;


    /**
     * Initializes the generator.
     *
     * @param configuration The config, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        xsdDirectoryName = PropertyUtils.getString(PROPKEY_XSD_DIR_NAME, configuration);
        complexTypeSuffix = PropertyUtils.getString(PROPKEY_XSD_COMPLEX_TYPE_SUFFIX, configuration);
    }


    /**
     * Generates the XSDs, and writes them to the target directory specified by the property {@link #PROPKEY_XSD_DIR_NAME}.
     */
    public void generateDataSetStructure() {
        File xsdDirectory = new File(xsdDirectoryName);
        logger.info("Creating data set xsd files in directory: " + xsdDirectory);

        xsdDirectory.mkdirs();

        generateDataSetXsd(xsdDirectory);
        for (DbSupport dbSupport : dbSupports) {
            generateSchemaXsd(dbSupport, xsdDirectory);
        }
        generateTemplateXml(xsdDirectory);
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

            String defaultSchemaName = defaultDbSupport.getSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"unitils\">\n");

            for (DbSupport dbSupport : dbSupports) {
                String schemaName = dbSupport.getSchemaName();
                writer.write("\t<xsd:import namespace=\"" + schemaName + "\" schemaLocation=\"" + schemaName + ".xsd\" />\n");
            }

            writer.write("\t<xsd:element name=\"dataset\">\n");
            writer.write("\t\t<xsd:complexType>\n");

            writer.write("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
            for (DbSupport dbSupport : dbSupports) {
                String schemaName = dbSupport.getSchemaName();
                writer.write("\t\t\t\t<xsd:any namespace=\"" + schemaName + "\"/>\n");
            }
            writer.write("\t\t\t</xsd:choice>\n");

            writer.write("\t\t<xsd:attribute name=\"caseSensitive\" use=\"optional\" type=\"xsd:boolean\" />\n");
            writer.write("\t\t<xsd:attribute name=\"literalToken\" use=\"optional\" type=\"xsd:string\" />\n");
            writer.write("\t\t<xsd:attribute name=\"variableToken\" use=\"optional\" type=\"xsd:string\" />\n");
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
     * @param dbSupport    The db support, not null
     * @param xsdDirectory The target directory, not null
     */
    protected void generateSchemaXsd(DbSupport dbSupport, File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, dbSupport.getSchemaName() + ".xsd")));

            String schemaName = dbSupport.getSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns=\"" + schemaName + "\" targetNamespace=\"" + schemaName + "\">\n");

            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                writer.write("\t<xsd:element name=\"" + tableName + "\" type=\"" + tableName + complexTypeSuffix + "\" />\n");
            }

            for (String tableName : tableNames) {
                writer.write("\t<xsd:complexType name=\"" + tableName + complexTypeSuffix + "\">\n");
                writer.write("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
                writer.write("\t\t\t\t<xsd:any namespace=\"" + schemaName + "\"/>\n");
                writer.write("\t\t\t</xsd:choice>\n");

                Set<String> columnNames = dbSupport.getColumnNames(tableName);
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


    /**
     * Generates a template xml file that uses the XSDs.
     *
     * @param xsdDirectory The target directory, not null
     */
    protected void generateTemplateXml(File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, "dataset-template.xml")));

            String defaultSchemaName = defaultDbSupport.getSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<unitils:dataset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            writer.write("\t\t\txmlns=\"" + defaultSchemaName + "\"");
            for (DbSupport dbSupport : dbSupports) {
                String schemaName = dbSupport.getSchemaName();
                writer.write(" xmlns:" + schemaName + "=\"" + schemaName + "\"");
            }
            writer.write(" xmlns:unitils=\"unitils\"\n");
            writer.write("\t\t\txsi:schemaLocation=\"");
            for (DbSupport dbSupport : dbSupports) {
                String schemaName = dbSupport.getSchemaName();
                writer.write(schemaName + " " + schemaName + ".xsd ");
            }
            writer.write("unitils dataset.xsd\">\n\n\n");
            writer.write("</unitils:dataset>\n");

        } catch (Exception e) {
            throw new UnitilsException("Error generating template xml file: " + xsdDirectory, e);
        } finally {
            closeQuietly(writer);
        }
    }

}