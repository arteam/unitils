/*
 * Copyright 2012,  Unitils.org
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

    // todo unit test

    public UnitilsException() {
    }

    public UnitilsException(String message) {
        super(message);
    }

    public UnitilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnitilsException(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        String reason = "";

        Throwable cause = getCause();
        if (cause != null) {
            if (!isBlank(cause.getMessage())) {
                reason = "\nReason: ";
                if (!(cause instanceof UnitilsException)) {
                    reason += cause.getClass().getSimpleName() + ": ";
                }
                reason = reason + cause.getMessage();
            }
        }
        return super.getMessage() + reason;
    }
}
