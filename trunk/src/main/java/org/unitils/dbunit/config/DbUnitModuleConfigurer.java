package org.unitils.dbunit.config;

import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.core.util.ConfigUtils;
import org.unitils.core.Unitils;
import org.unitils.util.PropertyUtils;
import org.unitils.util.ReflectionUtils;
import org.unitils.database.DatabaseModule;
import org.dbmaintain.dbsupport.DbSupport;

import java.util.Properties;

/**
 * This class is in charge of the configuration of all objects used by the {@link org.unitils.dbunit.DbUnitModule}.
 * <p/>
 * This class is the first step in the introduction of using the IOC pattern into unitils: a Configurer should come into
 * existence for each module and initialize and inject the dependencies of the module and all the helper objects that
 * it uses. For now, it uses reflection to instantiate each class. This is for backwards compatibility: In the end we
 * want to obtain that only the Configurers are instantiated using reflection: If a user then wants to switch the
 * implementation of a unitils class, he has to extend the default implementation of the Configurer and override the
 * method that instantiates the object for which a custom implementation must be used.
 *
 * @author Filip Neven
 */
public class DbUnitModuleConfigurer {

    /* Property key for the target directory for the generated xsd files */
    public static final String PROPKEY_XSD_DIR_NAME = "dataSetStructureGenerator.xsd.dirName";

    /* Property key for the suffix to use when defining complex types for the table definitions */
    public static final String PROPKEY_XSD_COMPLEX_TYPE_SUFFIX = "dataSetStructureGenerator.xsd.complexTypeSuffix";
    
    private final Properties configuration;

    public DbUnitModuleConfigurer(Properties configuration) {
        this.configuration = configuration;
    }

    public DataSetStructureGenerator getDataSetStructureGenerator() {
        String xsdDirectoryName = PropertyUtils.getString(PROPKEY_XSD_DIR_NAME, configuration);
        String complexTypeSuffix = PropertyUtils.getString(PROPKEY_XSD_COMPLEX_TYPE_SUFFIX, configuration);
        DbSupport defaultDbSupport = getDatabaseModule().getDbMaintainFacade().getDefaultDbSupport();
        Class<DataSetStructureGenerator> structureGeneratorClass = ConfigUtils.getConfiguredClass(DataSetStructureGenerator.class, configuration);
        return ReflectionUtils.createInstanceOfType(structureGeneratorClass, false,
                new Class[] {String.class, String.class, DbSupport.class},
                new Object[] {xsdDirectoryName, complexTypeSuffix, defaultDbSupport}
        );
    }


    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }
}
