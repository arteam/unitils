/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbunit.datasetoperation;

/**
 * Dummy class that represents the default {@link DataSetOperation}. Can be used in the {@link org.unitils.dbunit.annotation.DataSet}
 * annotation to indicate that the {@link DataSetOperation} configured in the unitils configuration must be used.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DefaultDataSetOperation implements DataSetOperation {
}
