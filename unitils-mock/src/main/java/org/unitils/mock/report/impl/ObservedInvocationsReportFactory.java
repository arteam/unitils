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

import org.unitils.core.Factory;
import org.unitils.core.annotation.Property;
import org.unitils.core.util.ObjectFormatter;

/**
 * @author Tim Ducheyne
 */
public class ObservedInvocationsReportFactory implements Factory<ObservedInvocationsReport> {

    protected Integer objectFormatterMaxDepth;
    protected Integer objectFormatterMaxNrArrayOrCollectionElements;
    protected Integer maxInlineParameterLength;


    public ObservedInvocationsReportFactory(@Property(value = "mock.objectFormatterMaxDepth", optional = true) Integer objectFormatterMaxDepth,
                                            @Property(value = "mock.objectFormatterMaxNrArrayOrCollectionElements", optional = true) Integer objectFormatterMaxNrArrayOrCollectionElements,
                                            @Property(value = "mock.maxInlineParameterLength", optional = true) Integer maxInlineParameterLength) {
        this.objectFormatterMaxDepth = objectFormatterMaxDepth;
        this.objectFormatterMaxNrArrayOrCollectionElements = objectFormatterMaxNrArrayOrCollectionElements;
        this.maxInlineParameterLength = maxInlineParameterLength;
    }


    public ObservedInvocationsReport create() {
        if (maxInlineParameterLength == null) {
            maxInlineParameterLength = 50;
        }
        ObjectFormatter objectFormatter = new ObjectFormatter(objectFormatterMaxDepth, objectFormatterMaxNrArrayOrCollectionElements);
        return new ObservedInvocationsReport(objectFormatter, maxInlineParameterLength);
    }
}
