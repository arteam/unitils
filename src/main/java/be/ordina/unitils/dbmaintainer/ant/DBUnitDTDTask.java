package be.ordina.unitils.dbmaintainer.ant;

import be.ordina.unitils.dbmaintainer.dtd.DtdGenerator;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tools.ant.BuildException;

/**
 * Ant task that generates a DTD from the unit test database.
 */
public class DBUnitDTDTask extends BaseUnitilsTask {

    /* Property key of the impelementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    public void doExecute() throws BuildException {

        Configuration configuration = UnitilsConfiguration.getInstance();
        DtdGenerator dtdGenerator = ReflectionUtils.getInstance(configuration.getString(PROPKEY_DTDGENERATOR_CLASSNAME));
        dtdGenerator.init(dataSource);
        dtdGenerator.generateDtd();
    }

}
