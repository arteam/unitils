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
package org.unitils.dbmaintainer.maintainer.version;

import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Interface that gives access to the version of a database, and a means to increment this version. The version of
 * a database is represented by a {@link Version} object. This interface can also be used to register / retrieve whether
 * the lastest database update succeeded.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface VersionSource {


    /**
     * @return The current version of the database
     */
    Version getDbVersion() throws StatementHandlerException;


    /**
     * Updates the version of the database to the given value
     *
     * @param version The new version that the database should be updated to
     */
    void setDbVersion(Version version) throws StatementHandlerException;


    /**
     * Tells us whether the last database version update succeeded or not
     *
     * @return true if the last database version update succeeded, false otherwise
     */
    boolean lastUpdateSucceeded();


    /**
     * Notifies the VersionSource of the fact that the lastest version update has succeeded or not
     *
     * @param succeeded True for success
     */
    void registerUpdateSucceeded(boolean succeeded) throws StatementHandlerException;
}
