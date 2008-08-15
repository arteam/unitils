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
package org.unitils.mock;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.core.AssertStatementCallRegistratingMethodInterceptor;
import org.unitils.mock.core.InvocationMatcherBuilder;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.MockObjectProxy;
import org.unitils.mock.core.MockObjectProxyMethodInterceptor;
import org.unitils.mock.core.ProxyUtils;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.argumentmatcher.EqualsArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.LenEqArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.NullArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.RefEqArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.SameArgumentMatcher;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockUnitils {

	private static InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();


	@SuppressWarnings("unchecked")
	public static <T> MockBehaviorDefiner<T> mock(T mock) {
		if (!MockObjectProxy.class.isAssignableFrom(mock.getClass())) {
			throw new UnitilsException(mock + " is not a mock object");
		}
		MockObject<T> mockObject = ((MockObjectProxy<T>)mock).$_$_getMockObject();
		MockBehaviorDefiner<T> mockBehaviorDefiner = new MockBehaviorDefiner<T>(mockObject);
		return mockBehaviorDefiner;
	}
	

	public static <T> T assertInvoked(T mock) {
		MockObject<T> mockObject = getMockObjectFromProxy(mock);
		AssertStatementCallRegistratingMethodInterceptor<T> assertStatementCallRegistratingMethodInterceptor = 
			new AssertStatementCallRegistratingMethodInterceptor<T>(getScenario(), mockObject, true);
		return ProxyUtils.createProxy(assertStatementCallRegistratingMethodInterceptor, mockObject.getMockedClass(), MockObjectProxy.class);
	}
	
	
	public static <T> T assertNotInvoked(T mock) {
		MockObject<T> mockObject = getMockObjectFromProxy(mock);
		AssertStatementCallRegistratingMethodInterceptor<T> assertStatementCallRegistratingMethodInterceptor = 
			new AssertStatementCallRegistratingMethodInterceptor<T>(getScenario(), mockObject, false);
		return ProxyUtils.createProxy(assertStatementCallRegistratingMethodInterceptor, mockObject.getMockedClass(), MockObjectProxy.class);
	}

	@SuppressWarnings("unchecked")
	private static <T> MockObject<T> getMockObjectFromProxy(T proxy) {
		if (!MockObjectProxy.class.isAssignableFrom(proxy.getClass())) {
			throw new UnitilsException(proxy + " is not a mock object");
		}
		MockObject<T> mockObject = ((MockObjectProxy<T>)proxy).$_$_getMockObject();
		return mockObject;
	}
	
	
	public static void assertNoMoreInvocations() {
		getScenario().assertNoMoreInvocations();
	}
	
	
	/**
     * Creates a mock object of the given type, associated to the given {@link Scenario}.
     *
     * @param <T>             the type of the mock
     * @param mockedClass        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(String name, Class<T> mockType, Scenario scenario) {
    	return createMock(name, mockType, false, scenario);
    }
    
    
    /**
     * Creates a mock object of the given type, associated to the given {@link Scenario}.
     *
     * @param <T>             the type of the mock
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createPartialMock(String name, Class<T> mockType, Scenario scenario) {
    	return createMock(name, mockType, true, scenario);
    }
	
	
	private static <T> T createMock(String name, Class<T> mockType, boolean invokeOriginalMethodIfNoBehavior, Scenario scenario) {
		MockObject<T> mockObject = new MockObject<T>(name, mockType, invokeOriginalMethodIfNoBehavior, scenario);
    	
		MockObjectProxyMethodInterceptor<T> mockObjectProxyMethodInterceptor = new MockObjectProxyMethodInterceptor<T>(mockObject);
		return ProxyUtils.createProxy(mockObjectProxyMethodInterceptor, mockType, MockObjectProxy.class);
	}
	
	
	@ArgumentMatcher
	public static <T> T notNull(Class<T> argumentClass) {
		invocationMatcherBuilder.registerArgumentMatcher(new NotNullArgumentMatcher());
		return null;
	}
	
	
	@ArgumentMatcher
	public static <T> T isNull(Class<T> argumentClass) {
		invocationMatcherBuilder.registerArgumentMatcher(new NullArgumentMatcher());
		return null;
	}
	
	
	@ArgumentMatcher
	public static <T> T same(T sameAs) {
		invocationMatcherBuilder.registerArgumentMatcher(new SameArgumentMatcher(sameAs));
		return null;
	}
	
	
	@ArgumentMatcher
	public static <T> T eq(T equalTo) {
		invocationMatcherBuilder.registerArgumentMatcher(new EqualsArgumentMatcher(equalTo));
		return null;
	}
	
	
	@ArgumentMatcher
	public static <T> T refEq(T equalTo) {
		invocationMatcherBuilder.registerArgumentMatcher(new RefEqArgumentMatcher(equalTo));
		return null;
	}
	
	
	@ArgumentMatcher
	public static <T> T lenEq(T equalTo) {
		invocationMatcherBuilder.registerArgumentMatcher(new LenEqArgumentMatcher(equalTo));
		return null;
	}
	
	
	protected static Scenario getScenario() {
		return getMockModule().getScenario();
	}
	
	
	private static MockModule getMockModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(MockModule.class);
	}
}
