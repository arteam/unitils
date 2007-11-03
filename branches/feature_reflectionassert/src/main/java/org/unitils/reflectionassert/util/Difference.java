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
package org.unitils.reflectionassert.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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

    /* When isEquals is false this will contain the stack of the fieldnames where the difference was found. <br>
     * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA". */
    protected Stack<String> fieldStack;

    // todo javadoc
    private Map<String, Difference> childDifferences;

    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     * @param fieldStack the current field names
     */
    public Difference(String message, Object leftValue, Object rightValue, Stack<String> fieldStack) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.message = message;
        this.fieldStack = fieldStack;
        this.childDifferences = new HashMap<String, Difference>();
    }

    /**
     * Gets a string representation of the field stack.
     * Eg primitiveFieldInB.fieldBinA.fieldA
     * The top-level element is an empty string.
     *
     * @return the field names as sting

    public String getFieldStackAsString() {
    String result = "";
    Iterator<Map.Entry<String, Difference>> iterator = childDifferences.entrySet().iterator();
    if (iterator.hasNext()) {
    Map.Entry<String, Difference> entry = iterator.next();

    result = entry.getKey();
    String childFieldStackAsString = entry.getValue().getFieldStackAsString();
    if (!"".equals(childFieldStackAsString)) {
    result += "." + entry.getValue().getFieldStackAsString();
    }
    }
    return result;
    }*/


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


    //todo javadoc
    public void addChildDifference(String fieldName, Difference difference) {
        childDifferences.put(fieldName, difference);
    }


    public Map<String, Difference> getChildDifferences() {
        return childDifferences;
    }


    /**
     * Gets a string representation of the field stack.
     * Eg primitiveFieldInB.fieldBinA.fieldA
     * The top-level element is an empty string.
     *
     * @return the field names as sting
     */
    public String getFieldStackAsString() {
        String result = "";
        Iterator<?> iterator = fieldStack.iterator();
        while (iterator.hasNext()) {
            result += iterator.next();
            if (iterator.hasNext()) {
                result += ".";
            }
        }
        return result;
    }

    /**
     * Gets the stack of the fieldnames where the difference was found.
     * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA".
     * The top-level element has an empty stack.
     *
     * @return the stack of field names, not null
     */
    public Stack<String> getFieldStack() {
        return fieldStack;
    }

}