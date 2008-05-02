/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.reflectionassert.formatter;

import org.apache.commons.lang.StringUtils;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.formatter.impl.DefaultDifferenceFormatter;
import org.unitils.reflectionassert.formatter.impl.TreeDifferenceFormatter;

/**
 * Creates a report of the given differences. This will first output the differences using the default difference
 * formatter. If the difference is not a simple difference, this will also output the difference tree using the
 * difference tree formatter.
 *  
 * todo create interface + property
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DifferenceReport {


    /**
     * Creates a report.
     *
     * @param message    An optional message
     * @param difference The difference to output, null for a match
     * @return The report, not null
     */
    public static String createReport(String message, Difference difference) {
        if (difference == null) {
            return "Found no differences.";
        }

        String result = "";
        if (!StringUtils.isEmpty(message)) {
            result += message + "\n\n";
        }

        result += "Found following differences:\n\n";
        result += new DefaultDifferenceFormatter().format(difference);
        if (!Difference.class.equals(difference.getClass())) {
            result += "\nDifference object tree:\n\n";
            result += new TreeDifferenceFormatter().format(difference);
        }
        return result;
    }

}
