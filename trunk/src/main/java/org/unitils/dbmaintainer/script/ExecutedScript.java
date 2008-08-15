/*
 * Copyright 2008,  Unitils.org
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

import java.util.Date;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ExecutedScript {

	private Script script;
	
	private Date executedAt;
	
	private Boolean successful;

	public ExecutedScript(Script script, Date executedAt, Boolean successful) {
		this.script = script;
		this.executedAt = executedAt;
		this.successful = successful;
	}

	
	public Script getScript() {
		return script;
	}

	
	public Date getExecutedAt() {
		return executedAt;
	}

	
	public Boolean isSucceeded() {
		return successful;
	}


	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((script == null) ? 0 : script.hashCode());
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
		final ExecutedScript other = (ExecutedScript) obj;
		if (script == null) {
			if (other.script != null)
				return false;
		} else if (!script.equals(other.script))
			return false;
		return true;
	}
	

}
