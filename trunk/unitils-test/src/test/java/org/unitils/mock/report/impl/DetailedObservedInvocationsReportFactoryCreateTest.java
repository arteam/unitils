/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.mock.report.impl;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class DetailedObservedInvocationsReportFactoryCreateTest extends UnitilsJUnit4 {

    private DetailedObservedInvocationsReportFactory detailedObservedInvocationsReportFactory;


    @Test
    public void create() {
        detailedObservedInvocationsReportFactory = new DetailedObservedInvocationsReportFactory(1, 2, 3);

        DetailedObservedInvocationsReport result = detailedObservedInvocationsReportFactory.create();
        assertPropertyReflectionEquals("objectFormatter.maxDepth", 1, result);
        assertPropertyReflectionEquals("objectFormatter.maxNrArrayOrCollectionElements", 2, result);
        assertPropertyReflectionEquals("maxInlineParameterLength", 3, result);
    }

    @Test
    public void defaultValues() {
        detailedObservedInvocationsReportFactory = new DetailedObservedInvocationsReportFactory(null, null, null);

        DetailedObservedInvocationsReport result = detailedObservedInvocationsReportFactory.create();
        assertPropertyReflectionEquals("objectFormatter.maxDepth", 3, result);
        assertPropertyReflectionEquals("objectFormatter.maxNrArrayOrCollectionElements", 15, result);
        assertPropertyReflectionEquals("maxInlineParameterLength", 50, result);
    }
}
