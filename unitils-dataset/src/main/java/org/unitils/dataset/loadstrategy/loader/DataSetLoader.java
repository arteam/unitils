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
package org.unitils.dataset.loadstrategy.loader;

import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.List;

/**
 * Loader for loading data set rows.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetLoader {

    /**
     * @param dataSetRowProcessor Processes data set rows so that they are ready to be loaded in the database, not null
     * @param databaseAccessor    The accessor for the database, not null
     */
    void init(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor);

    /**
     * Loads the rows provided by the given data set row source.
     *
     * @param dataSetRowSource The source that will provide the data set rows, not null
     * @param variables        The variable values that will be filled into the data set rows, not null
     */
    void load(DataSetRowSource dataSetRowSource, List<String> variables);

}