package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.dtd.DtdGenerator;
import org.unitils.util.ReflectionUtils;

/**
 * Ant task that generates a DTD from the unit test database.
 */
public class DBUnitDTDTask extends BaseUnitilsTask {

    /* Property key of the impelementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    public void doExecute() throws BuildException {

        Configuration configuration = Unitils.getInstance().getConfiguration();
        DtdGenerator dtdGenerator = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DTDGENERATOR_CLASSNAME));
        dtdGenerator.init(configuration, dataSource);
        dtdGenerator.generateDtd();
    }

}
