/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.mock.mockbehavior.impl;

import org.unitils.core.UnitilsException;
import org.unitils.core.util.ObjectToInjectHolder;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.unitils.core.util.ReflectionUtils.getGenericParameterType;
import static org.unitils.core.util.ReflectionUtils.isAssignable;

/**
 * @author Tim Ducheyne
 */
public class CollectionReturningMockBehavior implements ValidatableMockBehavior {

    /* The value to return */
    protected List<?> valuesToReturn;
    protected List<Type> valuesToReturnTypes;


    /**
     * Creates the list/set/array returning behavior for the given values.
     *
     * @param valuesToReturn The values
     */
    public CollectionReturningMockBehavior(Object... valuesToReturn) {
        this.valuesToReturn = unwrapValuesToReturnIfNeeded(valuesToReturn);
        this.valuesToReturnTypes = getValuesToReturnTypes(valuesToReturn);
    }


    /**
     * Checks whether the mock behavior can be executed for the given invocation.
     * An exception is raised if the method is a void method or does not have a list/set/array return type.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        if (valuesToReturn == null || valuesToReturn.isEmpty()) {
            return;
        }
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (returnType == Void.TYPE) {
            throw new UnitilsException("Trying to define mock behavior that returns a value for a void method.");
        }
        Type elementType;
        if (isAssignable(returnType, List.class) || isAssignable(returnType, Set.class)) {
            Type genericType = proxyInvocation.getMethod().getGenericReturnType();
            elementType = getGenericParameterType(genericType);
            if (elementType == null || elementType instanceof TypeVariable) {
                // raw type or not bound type: unable to verify whether the elements are of correct type
                // ignore, let java handle it at runtime
                return;
            }
        } else if (returnType.isArray()) {
            elementType = returnType.getComponentType();

        } else {
            throw new UnitilsException("Unable to return a list, set or array value. The method does not have a list, set or array return type.");
        }

        Type unAssignableType = isAssignableElementTypes(elementType);
        if (unAssignableType != null) {
            throw new UnitilsException("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: " + elementType + ", actual type: " + unAssignableType);
        }
    }

    protected Type isAssignableElementTypes(Type type) {
        if (valuesToReturnTypes == null) {
            return null;
        }
        for (Type valuesToReturnType : valuesToReturnTypes) {
            if (!isAssignable(valuesToReturnType, type)) {
                return valuesToReturnType;
            }
        }
        return null;
    }

    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The value
     */
    public Object execute(ProxyInvocation proxyInvocation) {
        if (valuesToReturn == null) {
            return null;
        }
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (isAssignable(returnType, Set.class)) {
            return new HashSet<Object>(valuesToReturn);
        }
        if (returnType.isArray()) {
            Object result = Array.newInstance(returnType.getComponentType(), valuesToReturn.size());
            return valuesToReturn.toArray((Object[]) result);
        }
        return valuesToReturn;
    }


    /**
     * If the value to return is an wrapper object, e.g. a mock, this will return the wrapped instance instead
     * of the wrapper.
     *
     * @param valuesToReturn The return values
     * @return The return value or the wrapped object if unwrapped
     */
    protected List<?> unwrapValuesToReturnIfNeeded(Object... valuesToReturn) {
        if (valuesToReturn == null) {
            return null;
        }
        List<Object> result = new ArrayList<Object>();
        for (Object valueToReturn : valuesToReturn) {
            if (valueToReturn instanceof ObjectToInjectHolder) {
                ObjectToInjectHolder objectToInjectHolder = (ObjectToInjectHolder) valueToReturn;
                valueToReturn = objectToInjectHolder.getObjectToInject();
            }
            result.add(valueToReturn);
        }
        return result;
    }

    protected List<Type> getValuesToReturnTypes(Object... valuesToReturn) {
        if (valuesToReturn == null) {
            return null;
        }
        List<Type> result = new ArrayList<Type>();
        for (Object valueToReturn : valuesToReturn) {
            if (valueToReturn == null) {
                continue;
            }
            Type type;
            if (valueToReturn instanceof ObjectToInjectHolder) {
                ObjectToInjectHolder objectToInjectHolder = (ObjectToInjectHolder) valueToReturn;
                type = objectToInjectHolder.getObjectToInjectType(null);
            } else {
                type = valueToReturn.getClass();
            }
            result.add(type);
        }
        return result;
    }
}
