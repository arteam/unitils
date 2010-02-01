/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.comparison;

import org.unitils.dataset.comparison.impl.DataSetComparison;
import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.loader.impl.Database;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetComparator {

    void init(Database database);

    DataSetComparison compare(DataSet expectedDataSet, List<String> variables);

}
