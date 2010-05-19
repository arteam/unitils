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
package org.unitils.dataset.core;

import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.loader.impl.RefreshDataSetLoader;
import org.unitils.dataset.util.DatabaseAccessor;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetStrategy extends BaseDataSetStrategy {

    @Override
    protected DataSetLoader createDataSetLoader(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor) {
        RefreshDataSetLoader refreshDataSetLoader = new RefreshDataSetLoader();
        refreshDataSetLoader.init(dataSetRowProcessor, databaseAccessor);
        return refreshDataSetLoader;
    }

}