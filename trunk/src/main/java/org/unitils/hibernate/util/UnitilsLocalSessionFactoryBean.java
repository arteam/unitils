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
package org.unitils.hibernate.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;

public class UnitilsLocalSessionFactoryBean extends LocalSessionFactoryBean {

	private Object testObject;
	
	private Method customConfigMethod;

	public void setTestObject(Object testObject) {
		this.testObject = testObject;
	}

	public void setCustomConfigMethod(Method customConfigMethod) {
		this.customConfigMethod = customConfigMethod;
	}

	@Override
	protected void postProcessMappings(Configuration config) throws HibernateException {
		if (customConfigMethod != null) {
			try {
				ReflectionUtils.invokeMethod(testObject, customConfigMethod, config);
			} catch (InvocationTargetException e) {
				throw new UnitilsException("Error while invoking custom config method", e.getCause());
			}
		}
	}
	
	
}
