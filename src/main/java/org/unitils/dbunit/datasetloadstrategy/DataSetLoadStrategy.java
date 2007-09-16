/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbunit.datasetloadstrategy;

import org.dbunit.dataset.IDataSet;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * Defines the contract for implementations that specify an operation that needs to be executed on the database, given
 * a DbUnit dataset. Implementations typically call an implementation of DbUnit's <code>DatabaseOperation</code> class.
 * Implementations must have an empty constructor so that an instance can be created using reflection.
 * <p/>
 * The concrete implementation class that is used can be configured using the annotation attribute
 * {@link org.unitils.dbunit.annotation.DataSet#loadStrategy()}. A default can be specified using the property
 * <code>DbUnitModule.DataSet.loadStrategy.default</code>.
 * <p/>
 * This wrapper mechanism makes it very easy to use custom DbUnit <code>DatabaseOperation</code> composite object
 * structures, without sacrificing the powerfulness of Unitils' configuration system.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSetLoadStrategy {

    /**
     * Executes this DataSetLoadStrategy. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet                  The dbunit dataset
     */
    public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet);
}
