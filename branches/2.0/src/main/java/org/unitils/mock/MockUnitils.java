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
package org.unitils.mock;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.InvocationMatcherBuilder;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.MockObjectProxy;
import org.unitils.mock.core.MockObjectProxyMethodInterceptor;
import org.unitils.mock.core.ProxyUtils;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.action.EmptyAction;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockUnitils {

	private static InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();


	@SuppressWarnings("unchecked")
	public static <T> MockBehaviorDefiner<T> mock(T mockObjectProxy) {
		if (!MockObjectProxy.class.isAssignableFrom(mockObjectProxy.getClass())) {
			throw new UnitilsException(mockObjectProxy + " is not a mock object");
		}
		MockObject<T> mockObject = ((MockObjectProxy<T>)mockObjectProxy).$_$_getMockObject();
		MockBehaviorDefiner<T> mockBehaviorDefiner = new MockBehaviorDefiner<T>(mockObject);
		return mockBehaviorDefiner;
	}
	
	
	public static <T> T createMock(Class<T> mockedClass, Scenario scenario) {
		MockObject<T> mockObject = new MockObject<T>(scenario, new EmptyAction(), mockedClass);
		MockObjectProxyMethodInterceptor<T> mockObjectProxyMethodInterceptor = new MockObjectProxyMethodInterceptor<T>(mockObject);
		return ProxyUtils.createProxy(mockObjectProxyMethodInterceptor, mockedClass, MockObjectProxy.class);
	}
	
	
	public static <T> T notNull(Class<T> argumentClass) {
		invocationMatcherBuilder.registerArgumentMatcher(new NotNullArgumentMatcher());
		return null;
	}
}
