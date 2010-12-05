/*
 * Copyright Unitils.org
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
package org.unitils.mock.core.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyFactory {

    private static Log logger = LogFactory.getLog(ProxyFactory.class);


    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     * If possible, the default constructor (can be private) will be used. If there is no default constructor,
     * no constructor will be called.
     *
     * @param mockName              The name of the mock, not null
     * @param invocationHandler     The handler that will handle the method invocations of the proxy, not null.
     * @param proxiedClass          The type to proxy, not null
     * @param implementedInterfaces Additional interfaces that the proxy must implement
     * @return The proxy object, not null
     */
    public static <T> T createProxy(String mockName, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        return createProxy(mockName, true, invocationHandler, proxiedClass, implementedInterfaces);
    }

    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     * No constructor or class-initialization will be called.
     *
     * @param mockName              The name of the mock, not null
     * @param invocationHandler     The handler that will handle the method invocations of the proxy, not null.
     * @param proxiedClass          The type to proxy, not null
     * @param implementedInterfaces Additional interfaces that the proxy must implement
     * @return The proxy object, not null
     */
    public static <T> T createUninitializedProxy(String mockName, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        return createProxy(mockName, false, invocationHandler, proxiedClass, implementedInterfaces);
    }


    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param mockName              The name of the mock, not null
     * @param initialize            If possible, use the default constructor and initialize all fields
     * @param implementedInterfaces Additional interfaces that the proxy must implement
     * @param proxiedClass          The type to proxy, not null
     * @param invocationHandler     The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */

    @SuppressWarnings({"unchecked"})
    protected static <T> T createProxy(String mockName, boolean initialize, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        Class<T> enhancedClass = createEnhancedClass(proxiedClass, implementedInterfaces);

        Factory proxy;
        if (initialize && !proxiedClass.isInterface()) {
            proxy = (Factory) createInitializedOrUninitializedInstanceOfType(enhancedClass);
        } else {
            proxy = (Factory) createUninitializedInstanceOfType(enhancedClass);
        }
        proxy.setCallbacks(new Callback[]{new CglibProxyMethodInterceptor(mockName, proxiedClass, invocationHandler)});
        return (T) proxy;
    }

    /**
     * Creates an instance of the given type. First we try to create an instance using the default constructor.
     * If this doesn't work, eg if there is no default constructor, we try using objenesis. This way the class doesn't
     * have to offer an empty constructor in order for this method to succeed.
     *
     * @param <T>   The type of the instance
     * @param clazz The class for which an instance is requested
     * @return An instance of the given class
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInitializedOrUninitializedInstanceOfType(Class<T> clazz) {
        try {
            return createInstanceOfType(clazz, true);
        } catch (UnitilsException e) {
            logger.warn("Could not create initialized instance of type " + clazz.getSimpleName() + ". No no-arg constructor found. All fields in the instance will have the java default values. Add a default constructor (can be private) if the fields should be initialized. If this concerns an innerclass, make sure it is declared static. Partial mocking of non-static innerclasses is not supported.");
        }
        // unable to create type using regular constuctor, try objenesis        
        return createUninitializedInstanceOfType(clazz);
    }

    /**
     * Creates an instance of the given type. First we try to create an instance using the default constructor.
     * No constructor or class-initialization will be called.
     *
     * @param <T>   The type of the instance
     * @param clazz The class for which an instance is requested
     * @return An instance of the given class
     */
    @SuppressWarnings("unchecked")
    public static <T> T createUninitializedInstanceOfType(Class<T> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        return (T) objenesis.newInstance(clazz);
    }


    @SuppressWarnings("unchecked")
    protected static <T> Class<T> createEnhancedClass(Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        Enhancer enhancer = new Enhancer();

        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        if (proxiedClass.isInterface()) {
            enhancer.setSuperclass(Object.class);
            interfaces.add(proxiedClass);
        } else {
            enhancer.setSuperclass(proxiedClass);
        }
        if (implementedInterfaces != null && implementedInterfaces.length > 0) {
            interfaces.addAll(asList(implementedInterfaces));
        }
        if (!interfaces.isEmpty()) {
            enhancer.setInterfaces(interfaces.toArray(new Class<?>[interfaces.size()]));
        }
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        return enhancer.createClass();
    }

}