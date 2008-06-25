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
package org.unitils.reflectionassert.difference;

/**
 * A class for holding the difference between two objects.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Difference {

    /* The left result value */
    private Object leftValue;

    /* The right result value */
    private Object rightValue;

    /* A message describing the difference */
    private String message;


    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public Difference(String message, Object leftValue, Object rightValue) {
        this.message = message;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }


    /**
     * Gets the left value.
     *
     * @return the value
     */
    public Object getLeftValue() {
        return leftValue;
    }


    /**
     * Gets the right value.
     *
     * @return the value
     */
    public Object getRightValue() {
        return rightValue;
    }


    /**
     * Gets the message indicating the kind of difference.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }


    /**
     * Double dispatch method. Dispatches back to the given visitor.
     * <p/>
     * All subclasses should copy this method in their own class body.
     *
     * @param visitor  The visitor, not null
     * @param argument An optional argument for the visitor, null if not applicable
     * @return The result
     */
    public <T, A> T accept(DifferenceVisitor<T, A> visitor, A argument) {
        return visitor.visit(this, argument);
    }


}