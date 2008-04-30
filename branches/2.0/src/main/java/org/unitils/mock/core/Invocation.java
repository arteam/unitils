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
package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Invocation {

	private Object targetObject;
	
	private Method proxyMethod;
	
	private Method originalMethod;
	
	private List<?> arguments;
	
	private StackTraceElement invokedAt;


	public Invocation(Object targetObject, Method proxyMethod, Method originalMethod, List<?> arguments, StackTraceElement invokedAt) {
		this.targetObject = targetObject;
		this.proxyMethod = proxyMethod;
		this.originalMethod = originalMethod;
		this.arguments = arguments;
		this.invokedAt = invokedAt;
	}

	
	public Object getTargetObject() {
		return targetObject;
	}


	public Method getProxyMethod() {
		return proxyMethod;
	}


	public Method getOriginalMethod() {
		return originalMethod;
	}


	public List<?> getArguments() {
		return arguments;
	}

	
	public StackTraceElement getInvokedAt() {
		return invokedAt;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
