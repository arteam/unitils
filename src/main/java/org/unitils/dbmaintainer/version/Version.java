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
package org.unitils.dbmaintainer.version;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing the version of a database or the version of a script.
 * <p/>
 * A version is represented by a modification timestamp and a list of version indexes.
 * The indexes should be defined as follows:
 * <p/>
 * 01_folder/01_subfolder/1_script  ==> 1,1,1<br>
 * 01_folder/02_subfolder/1_script  ==> 1,2,1<br>
 * 01_folder/02_subfolder/script    ==> 1,2,null<br>
 * folder/subfolder/2_script        ==> null,null,2<br>
 * script                           ==> null<br>
 * <p/>
 * The last index should always be the index of the script.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Version implements Comparable<Version> {

    /* The version indexes, empty if not defined */
    private List<Long> indexes = new ArrayList<Long>();

    /* A last modification timestamp, expressed as the number of milliseconds since January 1, 1970 */
    private long timeStamp;


    /**
     * Creates a new version.
     *
     * @param indexes   The script indexes, not null
     * @param timeStamp The script timestamp
     */
    public Version(List<Long> indexes, long timeStamp) {
        this.indexes = indexes;
        this.timeStamp = timeStamp;
    }


    /**
     * Creates a new version.
     *
     * @param indexString The indexes as a string
     * @param timeStamp   The script timestamp
     */
    public Version(String indexString, long timeStamp) {
        this.indexes = extractIndexes(indexString);
        this.timeStamp = timeStamp;
    }


    /**
     * An empty list if no version is defined.
     *
     * @return The script index, not null
     */
    public List<Long> getIndexes() {
        return indexes;
    }


    /**
     * Gets the last index in the list.
     *
     * @return The last index, null if there is no last index
     */
    public Long getScriptIndex() {
        if (indexes.isEmpty()) {
            return null;
        }
        return indexes.get(indexes.size() - 1);
    }


    /**
     * Sets the indexes. Use an empty list if no version is defined.
     *
     * @param indexes The script indexes, not null
     */
    public void setIndexes(List<Long> indexes) {
        this.indexes = indexes;
    }


    /**
     * @return The script timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the script timestamp
     *
     * @param timeStamp The script timestamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * Gets a string representation of the indexes as followes:
     * 1, null, 2, null => 1.x.2.x
     *
     * @return The string, not null
     */
    public String getIndexesString() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (Long index : indexes) {
            if (first) {
                first = false;
            } else {
                result.append('.');
            }
            if (index == null) {
                result.append('x');
            } else {
                result.append(index);
            }
        }
        return result.toString();
    }


    /**
     * Extracts the indexes out of the given string as followes:
     * 1.x.2.x => 1, null, 2, null
     *
     * @param indexString The string
     * @return The list of longs or nulls in case of 'x'
     */
    protected List<Long> extractIndexes(String indexString) {
        List<Long> result = new ArrayList<Long>();
        if (isEmpty(indexString)) {
            return result;
        }

        String[] parts = split(indexString, '.');
        for (String part : parts) {
            if ("x".equalsIgnoreCase(part)) {
                result.add(null);
            } else {
                result.add(new Long(part));
            }
        }
        return result;
    }


    /**
     * @return The string representation of the version.
     */
    @Override
    public String toString() {
        return "indexes: " + getIndexesString() + ", timestamp: " + timeStamp;
    }


    /**
     * Compares the given version to this version using the index values.
     * <p/>
     * If both scripts have an index, the index is used.
     * If one of the scripts has an index, it is considerer lower than the script that does not have an index.
     *
     * @param otherVersion The other version, not null
     * @return -1 when this version is smaller, 0 if equal, 1 when larger
     */
    public int compareTo(Version otherVersion) {
        List<Long> otherIndexes = otherVersion.getIndexes();
        if (indexes.isEmpty()) {
            if (otherIndexes.isEmpty()) {
                return 0;
            }
            return -1;
        } else if (otherIndexes.isEmpty()) {
            return 1;
        }
        Iterator<Long> thisIterator = indexes.iterator();
        Iterator<Long> otherIterator = otherIndexes.iterator();

        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            Long thisIndex = thisIterator.next();
            Long otherIndex = otherIterator.next();

            if (thisIndex != null && otherIndex != null) {
                if (thisIndex < otherIndex) {
                    return -1;
                }
                if (thisIndex > otherIndex) {
                    return 1;
                }
            } else if (thisIndex != null) {
                return -1;
            } else if (otherIndex != null) {
                return 1;
            }
        }
        return 0;
    }
}
