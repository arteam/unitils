/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbmaintainer.script.impl;

/**
 * This Exception is thrown when a problem occurs handling a statement.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class StatementHandlerException extends Exception {


    /**
     * Constructs a StatementHandlerException with the given message
     *
     * @param message The exception message
     */
    public StatementHandlerException(String message) {
        super(message);
    }


    /**
     * Constructs a StatementHandlerException with the given message and cause
     *
     * @param message The exception message
     * @param cause   The wrapped exception
     */
    public StatementHandlerException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Constructs a StatementHandlerException with the given cause
     *
     * @param cause The wrapped exception
     */
    public StatementHandlerException(Throwable cause) {
        super(cause);
    }

}
