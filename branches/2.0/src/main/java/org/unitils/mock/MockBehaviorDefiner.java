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

import net.sf.cglib.proxy.MethodInterceptor;

import org.unitils.mock.core.Action;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.MockedMethodRegistratingMethodInterceptor;
import org.unitils.mock.core.MockBehaviorBuilder;
import org.unitils.mock.core.ProxyUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockBehaviorDefiner<T> {

	private MockBehaviorBuilder mockBehaviorBuilder = MockBehaviorBuilder.getInstance();
	
	public MockBehaviorDefiner(MockObject<T> mockObject) {
		super();
		mockBehaviorBuilder.registerMockObject(mockObject);
	}
	
	
	public T returns(Object returnValue) {
		mockBehaviorBuilder.registerReturnValue(returnValue, false);
		return getInvokedMethodRegistrator();
	}
	
	
	public T raises(Throwable exception) {
		mockBehaviorBuilder.registerThrownException(exception, false);
		return getInvokedMethodRegistrator();
	}
	
	
	public T performs(Action action) {
		mockBehaviorBuilder.registerPerformedAction(action, false);
		return getInvokedMethodRegistrator();
	}
	
	
	public T alwaysReturns(Object returnValue) {
		mockBehaviorBuilder.registerReturnValue(returnValue, true);
		return getInvokedMethodRegistrator();
	}
	
	
	public T alwaysRaises(Throwable exception) {
		mockBehaviorBuilder.registerThrownException(exception, true);
		return getInvokedMethodRegistrator();
	}
	
	
	public T alwaysPerforms(Action action) {
		mockBehaviorBuilder.registerPerformedAction(action, true);
		return getInvokedMethodRegistrator();
	}


	@SuppressWarnings("unchecked")
	protected T getInvokedMethodRegistrator() {
		MethodInterceptor invokedMethodRegistratingMethodInterceptor = new MockedMethodRegistratingMethodInterceptor<T>();
		return (T) ProxyUtils.createProxy(invokedMethodRegistratingMethodInterceptor, mockBehaviorBuilder.getMockObject().getMockedClass());
	}
}
