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

import org.unitils.dbmaintainer.util.DatabaseTask;


/**
 * Interface that gives access to the version of a database, and a means to increment this version. The version of
 * a database is represented by a {@link Version} object. This interface can also be used to register / retrieve whether
 * the lastest database update succeeded.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface VersionSource extends DatabaseTask {


    /**
     * @return The current version of the database
     */
    Version getDbVersion();


    /**
     * Updates the version of the database to the given value
     *
     * @param version The new version that the database should be updated to
     */
    void setDbVersion(Version version);


    /**
     * Tells us whether the last database version update succeeded or not
     *
     * @return True if the last database version update succeeded, false otherwise
     */
    boolean isLastUpdateSucceeded();


    /**
     * Notifies the VersionSource of the fact that the lastest version update has succeeded or not
     *
     * @param succeeded True for success
     */
    void setUpdateSucceeded(boolean succeeded);


    /**
     * Tells us whether the last database code update succeeded or not
     *
     * @return true if the last database code update succeeded, false otherwise
     */
    boolean isLastCodeUpdateSucceeded();


    /**
     * Notifies the VersionSource of the fact that the lastest code update has succeeded or not
     *
     * @param succeeded True for success
     */
    void setCodeUpdateSucceeded(boolean succeeded);


    /**
     * @return The current timestamp of the code scripts
     */
    long getCodeScriptsTimestamp();


    /**
     * Stores the timestamp of the code scripts in the VersionSource
     *
     * @param codeScriptsTimestamp The timestamp, not null
     */
    void setCodeScriptsTimestamp(long codeScriptsTimestamp);

}
