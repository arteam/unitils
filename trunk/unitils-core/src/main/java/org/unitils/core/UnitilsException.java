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
package org.unitils.core;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Exception type, used for all unrecoverable exceptions that occur in unitils.
 */
public class UnitilsException extends RuntimeException {

    public UnitilsException() {
    }

    public UnitilsException(String message) {
        super(message);
    }

    public UnitilsException(String message, Throwable cause) {
        super(message, cause);
    }


    @Override
    public String getMessage() {
        String message = super.getMessage();
        Throwable cause = getCause();
        if (message == null && cause == null) {
            return null;
        }
        String causeMessage = null;
        if (cause != null) {
            causeMessage = cause.getMessage();
        }

        StringBuilder result = new StringBuilder();
        if (!isBlank(message)) {
            result.append(message);
            if (!isBlank(causeMessage)) {
                result.append("\nReason: ");
            }
        }
        if (!isBlank(causeMessage)) {
            if (!(cause instanceof UnitilsException)) {
                result.append(cause.getClass().getSimpleName());
                result.append(": ");
            }
            result.append(causeMessage);
        }
        return result.toString();
    }
}
