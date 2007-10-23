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
package org.unitils.database.util;

/**
 * Marks a module as being flushable. This means that {@link #flushDatabaseUpdates(Object)} will be called on the module
 * when a flush is requested on the DatabaseModule (by calling its {@link #flushDatabaseUpdates(Object)} method).
 * <p/>
 * An example of when a module could need to be flushable is the HibernateModule. Hibernate sometimes stores
 * updates in the session (in memory) without performing them on the database. If you want to be sure that every such
 * update was performed on the database, you need to flush the hibernate session.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Flushable {

    /**
     * Flush all cached database operations.
     * @param testObject
     */
    void flushDatabaseUpdates(Object testObject);
}
