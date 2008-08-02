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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.unitils.dbmaintainer.util.DbScriptJarCreator;


/**
 * @author Alexander Snaps <alex.snaps@gmail.com>
 * @author Filip Neven
 */
public class DbScriptJarCreatorTask extends Task {

    private String jarFileName;
    
    private String location;
    private String extensions;
    private String postProcessingLocation;

    public void execute()
            throws BuildException {

        try {
			DbScriptJarCreator creator = new DbScriptJarCreator(location, extensions, postProcessingLocation);
            creator.createJar(jarFileName);
        } catch(Exception e) {
        	e.printStackTrace();
            throw new BuildException("Error creating jar file " + jarFileName, e);
        }
    }

    public void setJarFileName(String jarFileName) {
        this.jarFileName = jarFileName;
    }

	public void setLocation(String location) {
        this.location = location;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public void setPostProcessingLocation(String postProcessingLocation) {
        this.postProcessingLocation = postProcessingLocation;
    }
}
