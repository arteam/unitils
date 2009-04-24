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
package org.unitils.dbunit.dataset.comparison;

import org.unitils.dbunit.dataset.Value;

/**
 * A holder for 2 different values.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ValueDifference {

    /* The expected value, not null */
    private Value value;

    /* The actual value, null if the value was not found */
    private Value actualValue;


    /**
     * Creates a value difference.
     *
     * @param value       The expected value, not null
     * @param actualValue The actual value, null if the value was not found
     */
    public ValueDifference(Value value, Value actualValue) {
        this.value = value;
        this.actualValue = actualValue;
    }


    /**
     * @return The expected value, not null
     */
    public Value getValue() {
        return value;
    }


    /**
     * @return The actual value, null if the value was not found
     */
    public Value getActualValue() {
        return actualValue;
    }

}
