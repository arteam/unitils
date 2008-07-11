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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.unitils.dbmaintainer.version.Version;

/**
 * A class representing a script file and it's content.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Script implements Comparable<Script> {

    /* The name of the script */
    private String fileName;
    
    /* The version of the script */
    private Version version;
    
    private String targetDatabaseName;

    /* Timestamp that indicates when the file was last modified */
    private Long fileLastModifiedAt;
    
    /* Checksum calculated on the script contents */
    private String checkSum;
    
    /* The handle to the content of the script */
    private ScriptContentHandle scriptContentHandle;


    /**
     * Creates a script with the given script fileName, whose content is provided by the given handle.
     *
     * @param fileName The name of the script file, not null
     * @param fileLastModifiedAt 
     * @param scriptContentHandle Handle providing access to the contents of the script, not null
     */
    public Script(String fileName, Long fileLastModifiedAt, ScriptContentHandle scriptContentHandle) {
        this.fileName = fileName;
        this.version = getVersionFromPath(fileName);
        this.targetDatabaseName = getTargetDatabaseNameFromPath(fileName);
        this.fileLastModifiedAt = fileLastModifiedAt;
        this.scriptContentHandle = scriptContentHandle;
    }
    
    
	/**
     * Creates a script with the given fileName and content checksum. The contents of the scripts itself
     * are unknown, which makes a script that is created this way unsuitable for being executed. The reason
     * that we provide this constructor is to be able to store information of the script without having to
     * store it's contents. The availability of a checksum enables us to find out whether it's contents 
     * are equal to another script objects whose contents are provided.
     * 
     * @param fileName The name of the script file, not null
     * @param fileLastModifiedAt 
     * @param checkSum Checksum calculated for the content of the script
     */
    public Script(String fileName, Long fileLastModifiedAt, String checkSum) {
    	this.fileName = fileName;
    	this.version = getVersionFromPath(fileName);
    	this.targetDatabaseName = getTargetDatabaseNameFromPath(fileName);
    	this.fileLastModifiedAt = fileLastModifiedAt;
    	this.checkSum = checkSum;
    }


    /**
     * @return The script name, not null
     */
    public String getFileName() {
        return fileName;
    }
    
    
    /**
     * @return The version, not null
     */
    public Version getVersion() {
        return version;
    }
    
    
    /**
     * @return Logical name that indicates the target database on which this script must be executed. Can be null to
     * indicate that it must be executed on the default target database.
     */
    public String getTargetDatabaseName() {
		return targetDatabaseName;
	}


	/**
     * @return The timestamp at which the file in which this script is stored on the filesystem was last 
     * modified. Be careful: This can also be the timestamp on which this file was retrieved from the sourcde
     * control system. If the timestamp wasn't changed, we're almost 100% sure that this file has not been modified. If
     * changed, this file is possibly modified (but the reason might also be that a fresh checkout has been made
     * from the version control system, or that the script was applied from another workstation or from another copy
     * of the project), not null
     */
    public Long getFileLastModifiedAt() {
		return fileLastModifiedAt;
	}


	/**
     * @return Checksum calculated for the content of the script, not null
     */
	public String getCheckSum() {
		if (checkSum == null) {
			checkSum = scriptContentHandle.getCheckSum();
		}
		return checkSum;
	}


    /**
     * @return Handle that provides access to the content of the script. May be null! If so, this
     * object is not suitable for being executed. The checksum however cannot be null, so we can always
     * verify if the contents of the script are equal to another one.
     */
    public ScriptContentHandle getScriptContentHandle() {
		return scriptContentHandle;
	}


    /**
     * @param other Another script, not null
     * @param useLastModificationDates If true, this method first checks if the lastModifiedAt property of
     * this Script is equal to the given one. If equal, we assume that the contents are also equal and we don't 
     * compare the checksums. If not equal, we compare the checksums to find out whether there is a difference.
     * By setting this value to true, performance is heavily improved when you check for updates regularly from
     * the same workstation (which is the case when you use unitils's automatic database maintenance for testing).
     * This is because, to calculate a checksum, the script contents have to be read. This can take a few seconds
     * to complete, which we want to avoid since a check for database updates is started every time a test is launched
     * that accesses the test database.
     * For applying changes to an environment that can only be updated incrementally (e.g. a database use by testers
     * or even the production database), this parameter should be false, since working with last modification dates 
     * is not guaranteed to be 100% bulletproof (although unlikely, it is possible that a different version of 
     * the same file is checked out on different systems on exactly the same time).
     *  
     * @return True if the contents of this script are equal to the given one, false otherwise
     */
	public boolean isScriptContentEqualTo(Script other, boolean useLastModificationDates) {
		if (useLastModificationDates && this.getFileLastModifiedAt().equals(other.getFileLastModifiedAt())) {
			return true;
		}
		return this.getCheckSum().equals(other.getCheckSum());
	}


	/**
	 * @return True if this is an incremental script, i.e. it needs to be executed in the correct order, and it can
	 * be executed only once. If an incremental script is changed, the database needs to be recreated from scratch,
	 * or an error must be reported.
	 */
	public boolean isIncremental() {
		return version.getScriptIndex() != null;
	}


    /**
     * Compares the given script to this script by comparing the versions. Can be used to define
     * the proper execution sequence of the scripts.
     *
     * @param script The other script, not null
     * @return -1 when this script has a smaller version, 0 if equal, 1 when larger
     */
    public int compareTo(Script script) {
        return version.compareTo(script.getVersion());
    }
    
    /**
     * Creates a version for the given script file. The index -1 is used for files that do not have a version in the
     * file name.
     *
     * @param parentIndexes The indexes of the parent folders, not null
     * @param scriptFile    The script file, not null
     * @return The version of the script file, not null
     */
    protected Version createVersion(List<Long> parentIndexes, File scriptFile) {
        List<Long> indexes = new ArrayList<Long>();
        indexes.addAll(parentIndexes);
        indexes.add(extractIndex(scriptFile.getName()));
        return new Version(indexes);
    }
    
    
    protected Version getVersionFromPath(String relativePath) {
    	String[] pathParts = StringUtils.split(relativePath, '/');
    	List<Long> versionIndexes = new ArrayList<Long>();
    	for (String pathPart : pathParts) {
    		versionIndexes.add(extractIndex(pathPart));
    	}
    	return new Version(versionIndexes);
    }
    
    
    /**
     * Extracts the index part out of a given file name.
     *
     * @param pathPart The simple (only one part of path) directory or file name, not null
     * @return The index, null if there is no index
     */
    protected Long extractIndex(String pathPart) {
        if (StringUtils.contains(pathPart, "_")) {
        	String versionIndex = StringUtils.substringBefore(pathPart, "_");
        	try {
				return Long.parseLong(versionIndex);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }
    
    
    protected String getTargetDatabaseNameFromPath(String relativePath) {
    	String[] pathParts = StringUtils.split(relativePath, '/');
    	for (int i = pathParts.length - 1; i >= 0; i--) {
    		String targetDatabase = extractTargetDatabase(pathParts[i]);
    		if (targetDatabase != null) {
    			return targetDatabase;
    		}
    	}
    	return null;
	}
    
    protected String extractTargetDatabase(String pathPart) {
		Long index = extractIndex(pathPart);
		String pathPartAfterIndex;
		if (index == null) {
			pathPartAfterIndex = pathPart;
		} else {
			pathPartAfterIndex = StringUtils.substringAfter(pathPart, "_");
		}
		if (pathPartAfterIndex.startsWith("@") && pathPartAfterIndex.contains("_")) {
			String databaseName = StringUtils.substringBefore(pathPartAfterIndex, "_").substring(1);
			return databaseName;
		}
		return null;
	}

    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Script other = (Script) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}


	/**
     * Gets a string representation of this script.
     *
     * @return The name and version, not null
     */
    @Override
    public String toString() {
        return fileName + " " + getCheckSum();
    }


}
