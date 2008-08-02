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

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tools.ant.BuildException;
import org.unitils.dbmaintainer.util.DbScriptJarDatabaseResetter;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbScriptJarDatabaseResetterTask {

	private String jarFileName;
    private String databaseDialect;
    private String driverClassName;
    private String url;
    private String userName;
    private String schemaName;
    private String password;

    public void execute() throws BuildException {

    	try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(driverClassName);
			dataSource.setUrl(url);
			dataSource.setUsername(userName);
			dataSource.setPassword(password);
			new DbScriptJarDatabaseResetter(dataSource, databaseDialect,
					schemaName != null ? schemaName : userName)
					.resetDatabaseState(jarFileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
    }


    public void setJarFilename(String fileName) {
        this.jarFileName = fileName;
    }

    public void setDatabaseDialect(String databaseDialect) {
		this.databaseDialect = databaseDialect;
	}

	public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setPassword(String password) {
        this.password = password;
    }
}
