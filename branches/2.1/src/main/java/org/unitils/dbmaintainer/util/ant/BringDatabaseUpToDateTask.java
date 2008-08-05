/*
 * Copyright 2006-2008,  Unitils.org
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
 *
 * $Id$
 */
package org.unitils.dbmaintainer.util.ant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.util.DbScriptJarRunner;

/**
 * @author Alexander Snaps <alex.snaps@gmail.com>
 * @version $Revision$
 */
public class BringDatabaseUpToDateTask extends BaseDatabaseTask {

    private String jarFileName;
    private String extensions;
    private List<DatabaseType> databases = new ArrayList<DatabaseType>();

    public void execute() throws BuildException {

    	try {
			if (databases.size() == 0) {
				throw new UnitilsException("No target database defined");
			}
			DbSupport defaultDbSupport = null;
			Map<String, DbSupport> nameDbSupportMap = new HashMap<String, DbSupport>();
			for (DatabaseType database : databases) {
				DbSupport dbSupport = createDbSupport(database);
				nameDbSupportMap.put(dbSupport.getDatabaseName(), dbSupport);
				if (defaultDbSupport == null) {
					defaultDbSupport = dbSupport;
				}
			}
			new DbScriptJarRunner(defaultDbSupport, nameDbSupportMap, extensions).executeJar(jarFileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
    }

    public void setJarFilename(String fileName) {
        this.jarFileName = fileName;
    }
    
    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public void addDatabase(DatabaseType database) {
		databases.add(database);
	}
}
