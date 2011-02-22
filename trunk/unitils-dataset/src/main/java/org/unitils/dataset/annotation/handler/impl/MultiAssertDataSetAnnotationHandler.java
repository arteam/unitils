/*
 * Copyright DbMaintain.org
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
package org.unitils.dataset.annotation.handler.impl;

import org.unitils.dataset.DataSetModule;
import org.unitils.dataset.annotation.AssertDataSet;
import org.unitils.dataset.annotation.MultiAssertDataSet;

import java.lang.reflect.Method;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiAssertDataSetAnnotationHandler implements org.unitils.dataset.annotation.handler.DataSetAnnotationHandler<MultiAssertDataSet> {

    public void handle(MultiAssertDataSet annotation, Method testMethod, Object testInstance, DataSetModule dataSetModule) {
        AssertDataSetAnnotationHandler assertDataSetAnnotationHandler = new AssertDataSetAnnotationHandler();
        AssertDataSet[] assertDataSetAnnotations = annotation.value();
        for (AssertDataSet assertDataSetAnnotation : assertDataSetAnnotations) {
            assertDataSetAnnotationHandler.handle(assertDataSetAnnotation, testMethod, testInstance, dataSetModule);
        }
    }


}
