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
package org.unitils.reflectionassert.formatter;

import org.unitils.reflectionassert.difference.Difference;

/**
 * Creates a report of the given differences.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DifferenceReport {


    /**
     * Creates a report.
     *
     * @param message    An optional message
     * @param difference The difference to output, null for a match
     * @return The report, not null
     */
    public String createReport(String message, Difference difference);

}
