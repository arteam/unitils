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
	
	private Method method;
	
	private List<?> arguments;
	
	private StackTraceElement[] stackTrace;

	private MethodProxy methodProxy;

	public Invocation(Object targetObject, Method method, MethodProxy methodProxy, List<?> arguments, StackTraceElement[] stackTrace) {
		this.targetObject = targetObject;
		this.method = method;
		this.methodProxy = methodProxy;
		this.arguments = arguments;
		this.stackTrace = stackTrace;
	}

	
	public Object getTargetObject() {
		return targetObject;
	}


	public MethodProxy getMethodProxy() {
		return methodProxy;
	}


	public Method getMethod() {
		return method;
	}


	public List<?> getArguments() {
		return arguments;
	}

	
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
