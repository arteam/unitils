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
package org.unitils.dataset.loadstrategy.loader.impl;

import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.model.dataset.DataSetRow;

import java.util.List;

/**
 * Data set loader that first tries to update the data set row. If nothing was updated, it will use the {@link InsertDataSetLoader}
 * to insert the row in the database
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetLoader extends UpdateDataSetLoader {

    protected InsertDataSetLoader insertDataSetLoader;

    @Override
    public void init(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor) {
        super.init(dataSetRowProcessor, databaseAccessor);
        insertDataSetLoader = new InsertDataSetLoader();
        insertDataSetLoader.init(dataSetRowProcessor, databaseAccessor);
    }

    @Override
    protected void handleNoUpdatesPerformed() {
        // by default the update data set loader throws an exception if no update was performed
        // overridden to ignore this and then try the insert afterwards
    }

    @Override
    public int loadDataSetRow(DataSetRow dataSetRow, List<String> variables) {
        int nrUpdates = super.loadDataSetRow(dataSetRow, variables);
        if (nrUpdates > 0) {
            return nrUpdates;
        }
        return insertDataSetLoader.loadDataSetRow(dataSetRow, variables);
    }

}