package be.ordina.unitils.db.ant;

import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.db.dtd.DtdGenerator;
import org.apache.tools.ant.BuildException;
import org.apache.log4j.Logger;

/**
 * Ant task that generates a DTD from the unit test database.
 */
public class DBUnitDTDTask extends BaseUnitilsTask {

    private static final Logger logger = Logger.getLogger(ClearDatabaseTask.class);

    /* Property key of the impelementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    public void doExecute() throws BuildException {
        DtdGenerator dtdGenerator = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties,
                PROPKEY_DTDGENERATOR_CLASSNAME));
        dtdGenerator.init(properties, dataSource);
        dtdGenerator.generateDtd();
    }

}
