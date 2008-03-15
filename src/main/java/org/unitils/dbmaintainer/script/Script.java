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
package org.unitils.dbmaintainer.script;

import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.version.Version;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class representing a script file and it's content.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Script implements Comparable<Script> {

    /* The name of the script */
    private String name;

    /* The URL of the script */
    private URL url;

    /* The version of the script */
    private Version version;


    /**
     * Creates a script.
     *
     * @param name    The name of the script, not null
     * @param url     The URL of the script, not null
     * @param version The version, not null
     */
    public Script(String name, URL url, Version version) {
        this.name = name;
        this.url = url;
        this.version = version;
    }


    /**
     * Creates a script for the given file.
     *
     * @param scriptFile The script file, not null
     * @param version    The version, not null
     */
    public Script(File scriptFile, Version version) {
        this.name = scriptFile.getName();
        this.version = version;
        try {
            this.url = scriptFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UnitilsException("Unable to create URL for script " + name, e);
        }
    }


    /**
     * @return The script name, not null
     */
    public String getName() {
        return name;
    }


    /**
     * @return The version, not null
     */
    public Version getVersion() {
        return version;
    }


    /**
     * @return The URL of the script, not null
     */
    public URL getUrl() {
        return url;
    }


    /**
     * Opens a stream to the content of the script.
     * NOTE: do not forget to close the stream after usage.
     *
     * @return The content stream, not null
     */
    public Reader openScriptContentReader() throws UnitilsException {
        try {
            InputStream scriptInputStream = url.openStream();
            return new InputStreamReader(scriptInputStream);
        } catch (IOException e) {
            throw new UnitilsException("Error while trying to create reader for script " + name, e);
        }
    }


    /**
     * Compares the given script to this script by comparing the versions.
     *
     * @param script The other script, not null
     * @return -1 when this script has a smaller version, 0 if equal, 1 when larger
     */
    public int compareTo(Script script) {
        return version.compareTo(script.getVersion());
    }


    /**
     * Gets a string representation of this script.
     *
     * @return The name and version, not null
     */
    @Override
    public String toString() {
        return name + " " + version;
    }
}
