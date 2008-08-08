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
package org.unitils.dbmaintainer.script.impl.jar;

import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptContentHandle.UrlScriptContentHandle;
import org.unitils.dbmaintainer.util.DbScriptJarCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Alexander Snaps <alex.snaps@gmail.com>
 * @author Filip Neven
 */
public class DbScriptJarReader implements Iterable<Script> {

    private JarInputStream inputStream;
    private String jarFileName;
    private String encoding;

    public DbScriptJarReader(String jarFileName, String encoding) throws IOException {
        this.jarFileName = jarFileName;
        this.inputStream = new JarInputStream(new FileInputStream(new File(jarFileName)));
        this.encoding = encoding;
    }
    
    public void close() throws IOException {
        inputStream.close();
    }

    public Iterator<Script> iterator() {
        return new JarScriptIterator();
    }

    private class JarScriptIterator implements Iterator<Script> {

    	private JarEntry currentEntry;

    	public boolean hasNext() {
            try {
                do {
                	currentEntry = inputStream.getNextJarEntry();
                } while (currentEntry != null && currentEntry.getName().equals(DbScriptJarCreator.DBSCRIPT_JAR_PROPERTIES_FILENAME));
                return currentEntry != null;
            } catch(IOException e) {
                throw new UnitilsException("Error parsing jar file " + jarFileName, e);
            }
        }

        public Script next() {
            String fileName = currentEntry.getName();
            return new Script(fileName, currentEntry.getTime(), new UrlScriptContentHandle(getJarURL(fileName), encoding));
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }
    }

    private URL getJarURL(String filename) {
        try {
            return new URL(new StringBuilder("jar:file:")
                    .append(jarFileName)
                    .append("!/")
                    .append(filename)
                    .toString());
        } catch(MalformedURLException e) {
            throw new UnitilsException("Error creating URL out of script " + filename + " of jar file " + jarFileName);
        }
    }
}

