/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.reflectionassert.report.impl;

import org.apache.commons.lang.StringUtils;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.report.DifferenceReport;

/**
 * Creates a report of the given differences. This will first output the differences using the default difference
 * view. If the difference is not a simple difference, this will also output the difference tree using the
 * difference tree view.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDifferenceReport implements DifferenceReport {

    public static final int MAX_LINE_SIZE = 110;

    public static enum MatchType {NO_MATCH};

    /**
     * Creates a report.
     *
     * @param difference The difference to output, null for a match
     * @return The report, not null
     */
    public String createReport(Difference difference) {
        StringBuilder result = new StringBuilder();
        result.append(new SimpleDifferenceView().createView(difference)).append("\n\n");
        result.append("--- Found following differences ---\n");
        result.append(new DefaultDifferenceView().createView(difference));
        if (!Difference.class.equals(difference.getClass())) {
            result.append("\n--- Difference detail tree ---\n");
            result.append(new TreeDifferenceView().createView(difference));
        }
        return result.toString();
    }

}