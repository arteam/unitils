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
package org.unitils.dbmaintainer.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.util.StoredIdentifierCase;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbItemIdentifier {

	private String databaseName;
	
	private String schemaName;
	
	private String itemName;
	
	private boolean caseSensitive;
	
	private DbItemIdentifier(String databaseName, String schemaName, String itemName, boolean caseSensitive) {
		this.databaseName = databaseName;
		this.schemaName = schemaName;
		this.itemName = itemName;
		this.caseSensitive = caseSensitive;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getItemName() {
		return itemName;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public DbItemIdentifier getSchema() {
		return new DbItemIdentifier(databaseName, schemaName, null, caseSensitive);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
		result = prime * result + ((itemName == null) ? 0 : (caseSensitive ? itemName.hashCode() : itemName.toUpperCase().hashCode()));
		result = prime * result + ((schemaName == null) ? 0 : (caseSensitive ? schemaName.hashCode() : schemaName.toUpperCase().hashCode()));
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
		final DbItemIdentifier other = (DbItemIdentifier) obj;
		if (databaseName == null) {
			if (other.databaseName != null)
				return false;
		} else if (!databaseName.equals(other.databaseName))
			return false;
		if (itemName == null) {
			if (other.itemName != null)
				return false;
		} else if (!((caseSensitive && itemName.equals(other.itemName)) || (!caseSensitive && itemName.equalsIgnoreCase(itemName))))
			return false;
		if (schemaName == null) {
			if (other.schemaName != null)
				return false;
		} else if (!((caseSensitive && schemaName.equals(other.schemaName)) || (!caseSensitive && schemaName.equalsIgnoreCase(schemaName))))
			return false;
		return true;
	}
	
	
	public static DbItemIdentifier parseItemIdentifier(String identifierAsString, DbSupport defaultDbSupport, Map<String, DbSupport> dbNameDbSupportMap) {
		String[] identifierParts = StringUtils.split(identifierAsString, '.');
    	String databaseName, schemaName, itemName;
    	DbSupport dbSupport;
    	if (identifierParts.length == 3) {
    		databaseName = identifierParts[0];
    		dbSupport = dbNameDbSupportMap.get(databaseName);
    		if (dbSupport == null) {
    			throw new UnitilsException("No database configured with the name " + databaseName);
    		}
    		schemaName = identifierParts[1];
    		itemName = identifierParts[2];
    	} else if (identifierParts.length == 2) {
    		dbSupport = defaultDbSupport;
    		databaseName = dbSupport.getDatabaseName();
    		schemaName = identifierParts[0];
    		itemName = identifierParts[1];
    	} else if (identifierParts.length == 1) {
    		dbSupport = defaultDbSupport;
    		databaseName = dbSupport.getDatabaseName();
    		schemaName = dbSupport.getDefaultSchemaName();
    		itemName = identifierParts[0];
    	} else {
    		throw new UnitilsException("Incorrectly formatted db item identifier " + identifierAsString);
    	}
    	
    	return getItemIdentifier(schemaName, itemName, dbSupport);
	}
	
	
	public static DbItemIdentifier getItemIdentifier(String schemaName, String itemName, DbSupport dbSupport) {
		boolean caseSenstve = dbSupport.getStoredIdentifierCase() != StoredIdentifierCase.MIXED_CASE;
		if (!caseSenstve) {
    		schemaName = schemaName.toUpperCase();
    		itemName = itemName.toUpperCase();
    	} else {
    		schemaName = dbSupport.toCorrectCaseIdentifier(schemaName);
    		itemName = dbSupport.toCorrectCaseIdentifier(itemName);
    	}
    	return new DbItemIdentifier(dbSupport.getDatabaseName(), dbSupport.toCorrectCaseIdentifier(schemaName), 
    			dbSupport.toCorrectCaseIdentifier(itemName), caseSenstve);
	}
	
	
	public static DbItemIdentifier parseSchemaIdentifier(String identifierAsString, DbSupport defaultDbSupport, Map<String, DbSupport> dbNameDbSupportMap) {
		String[] identifierParts = StringUtils.split(identifierAsString, '.');
    	String databaseName, schemaName;
    	DbSupport dbSupport;
    	if (identifierParts.length == 2) {
    		databaseName = identifierParts[0];
    		dbSupport = dbNameDbSupportMap.get(databaseName);
    		if (dbSupport == null) {
    			throw new UnitilsException("No database configured with the name " + databaseName);
    		}
    		schemaName = identifierParts[1];
    	} else if (identifierParts.length == 1) {
    		dbSupport = defaultDbSupport;
    		databaseName = dbSupport.getDatabaseName();
    		schemaName = identifierParts[0];
    	} else {
    		throw new UnitilsException("Incorrectly formatted db schema identifier " + identifierAsString);
    	}
    	
    	return getSchemaIdentifier(schemaName, dbSupport);
	}

	
	public static DbItemIdentifier getSchemaIdentifier(String schemaName, DbSupport dbSupport) {
		boolean caseSenstve = dbSupport.getStoredIdentifierCase() != StoredIdentifierCase.MIXED_CASE;
		if (!caseSenstve) {
    		schemaName = schemaName.toUpperCase();
    	}
    	return new DbItemIdentifier(dbSupport.getDatabaseName(), dbSupport.toCorrectCaseIdentifier(schemaName), null, caseSenstve);
	}
}
