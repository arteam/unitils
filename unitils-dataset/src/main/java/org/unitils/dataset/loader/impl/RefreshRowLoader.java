/*
 * Copyright 2009,  Unitils.org
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

import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.loader.RowLoader;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshRowLoader extends UpdateRowLoader {

    protected RowLoader insertRowLoader;


    public void init(ColumnProcessor columnProcessor, NameProcessor nameProcessor, Database database) {
        super.init(columnProcessor, nameProcessor, database);
        insertRowLoader = new InsertRowLoader();
        insertRowLoader.init(columnProcessor, nameProcessor, database);
    }

    @Override
    protected void handleNoUpdatesPerformed() {
    }

    public int loadRow(Row row, List<String> variables) {
        int nrUpdates = super.loadRow(row, variables);
        if (nrUpdates > 0) {
            return nrUpdates;
        }
        return insertRowLoader.loadRow(row, variables);
    }

}