package org.unitils.dbmaintainer.structure.impl;

import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import org.unitils.util.PropertyUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

/**
 * Implementation of {@link DataSetStructureGenerator} that generates xml schema files for data sets.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class XsdDataSetStructureGenerator extends BaseDatabaseTask implements DataSetStructureGenerator {

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
    protected void doInit(Properties configuration) {
        xsdDirectoryName = PropertyUtils.getString(PROPKEY_XSD_DIR_NAME, configuration);
        complexTypeSuffix = PropertyUtils.getString(PROPKEY_XSD_COMPLEX_TYPE_SUFFIX, configuration);
    }


    /**
     * todo javadoc
     * Generates the DTD, and writes it to the file specified by the property {@link #PROPKEY_XSD_DIR_NAME}.
     * The DTD will contain the structure of the dataabase. All tables will be written as optional elements and
     * all columns will be optional attributes.
     */
    public void generateDataSetStructure() {
        File xsdDirectory = new File(xsdDirectoryName);
        xsdDirectory.mkdirs();

        generateDataSetXsd(xsdDirectory);
        for (DbSupport dbSupport : dbSupports) {
            generateDatabaseSchemaXsd(dbSupport, xsdDirectory);
        }
    }


    protected void generateDataSetXsd(File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, "dataset.xsd")));

            String defaultSchemaName = defaultDbSupport.getSchemaName();
            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns:dflt=\"" + defaultSchemaName + " \">\n");

            for (DbSupport dbSupport : dbSupports) {
                String schemaName = dbSupport.getSchemaName();
                writer.write("\t<xsd:import namespace=\"" + schemaName + "\" schemaLocation=\"" + schemaName + ".xsd\" />\n");
            }

            writer.write("\t<xsd:element name=\"dataset\">\n");
            writer.write("\t\t<xsd:complexType>\n");
            writer.write("\t\t\t<xsd:choice minOccurs=\"1\" maxOccurs=\"unbounded\">\n");

            Set<String> defaultSchemaTableNames = defaultDbSupport.getTableNames();
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


    protected void generateDatabaseSchemaXsd(DbSupport dbSupport, File xsdDirectory) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(xsdDirectory, dbSupport.getSchemaName() + ".xsd")));

            writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            writer.write("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"" + dbSupport.getSchemaName() + " \">\n");

            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                writer.write("\t<xsd:element name=\"" + tableName + "\" type=\"" + tableName + complexTypeSuffix + "\" />\n");
            }

            for (String tableName : tableNames) {
                writer.write("\t<xsd:complexType name=\"" + tableName + complexTypeSuffix + "\">\n");

                Set<String> columnNames = dbSupport.getColumnNames(tableName);
                Set<String> pkColumnNames = dbSupport.getPrimaryKeyColumnNames(tableName);
                for (String columnName : columnNames) {
                    String use = pkColumnNames.contains(columnName) ? "required" : "optional";
                    writer.write("\t\t<xsd:attribute name=\"" + columnName + "\" use=\"" + use + "\" />\n");
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
