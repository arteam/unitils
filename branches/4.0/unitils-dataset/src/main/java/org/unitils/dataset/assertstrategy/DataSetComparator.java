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
package org.unitils.dataset.assertstrategy;

import org.unitils.dataset.assertstrategy.impl.TableContentRetriever;
import org.unitils.dataset.assertstrategy.model.DataSetComparison;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetComparator {

    void init(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever, DataSourceWrapper dataSourceWrapper);

    DataSetComparison compare(DataSetRowSource expectedDataSetRowSource, List<String> variables);

}
