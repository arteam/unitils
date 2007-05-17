package org.unitils.dbunit.datasetoperation;

import org.dbunit.dataset.IDataSet;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * Dummy class that represents the default {@link DataSetOperation}. Can be used in the {@link org.unitils.dbunit.annotation.DataSet}
 * annotation to indicate that the {@link DataSetOperation} configured in the unitils configuration must be used.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DefaultDataSetOperation implements DataSetOperation {
}
