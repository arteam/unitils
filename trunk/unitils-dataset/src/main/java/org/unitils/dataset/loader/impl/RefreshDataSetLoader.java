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
package org.unitils.dataset.loader.impl;

import org.unitils.dataset.core.dataset.DataSetRow;
import org.unitils.dataset.core.impl.DataSetRowProcessor;
import org.unitils.dataset.database.DatabaseAccessor;

import java.util.List;

/**
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