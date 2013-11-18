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
package org.unitils.mock.core.proxy;

/**
 * @author Tim Ducheyne
 */
public class Argument<T> {

    protected T value;
    protected T valueAtInvocationTime;
    protected Class<? extends T> type;


    public Argument(T value, T valueAtInvocationTime, Class<? extends T> type) {
        this.value = value;
        this.valueAtInvocationTime = valueAtInvocationTime;
        this.type = type;
    }


    public T getValue() {
        return value;
    }

    /**
     * @return A deep clone of the value at the time that they it was used (pass by value), the value itself if it was not possible to perform the clone
     */
    public T getValueAtInvocationTime() {
        return valueAtInvocationTime;
    }

    public Class<? extends T> getType() {
        return type;
    }
}
