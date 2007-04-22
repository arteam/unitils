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
package org.unitils.dbmaintainer.version;

/**
 * Class representing the version of a database, or the version of a number of DDL scripts.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Version {

    /* The index of the last (executed) script */
    private Long index;

    /* The timestamp of the script that was last modified, expressed as the number of milliseconds since January 1, 1970 */
    private Long timeStamp;


    /**
     * Creates a new version.
     *
     * @param index     The script index, not null
     * @param timeStamp The script timestamp, not null
     */
    public Version(Long index, Long timeStamp) {
        this.index = index;
        this.timeStamp = timeStamp;
    }


    /**
     * @return The script index, not null
     */
    public Long getIndex() {
        return index;
    }


    /**
     * @return The script timestamp, not null
     */
    public Long getTimeStamp() {
        return timeStamp;
    }


    /**
     * @return The string representation of the version.
     */
    public String toString() {
        return "Index " + index + "; timestamp = " + timeStamp;
    }

}
