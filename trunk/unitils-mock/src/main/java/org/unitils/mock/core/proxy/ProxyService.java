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
import org.unitils.core.util.ObjectToFormat;
import org.unitils.mock.core.util.CloneService;

import java.util.*;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Arrays.asList;
import static org.springframework.util.ClassUtils.isCglibProxyClassName;
import static org.unitils.core.util.ReflectionUtils.createInstanceOfType;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Tim Ducheyne
 * @author Kenny Claes
 * @author Filip Neven
 */
public class ProxyService {

    protected static Log logger = LogFactory.getLog(ProxyService.class);

    protected CloneService cloneService;


    public ProxyService(CloneService cloneService) {
        this.cloneService = cloneService;
    }


    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param name                  A display name for the proxy, not null
     * @param initialize            If possible, use the default constructor and initialize all fields
     * @param implementedInterfaces Additional interfaces that the proxy must implement
     * @param proxiedClass          The type to proxy, not null
     * @param invocationHandler     The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(String name, boolean initialize, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        Class<T> enhancedClass;
        try {
            enhancedClass = createEnhancedClass(proxiedClass, implementedInterfaces);
        } catch (Exception e) {
            throw new UnitilsException("Unable to create proxy with name " + name + " for type " + proxiedClass, e);
        }

        Factory proxy;
        if (initialize && !proxiedClass.isInterface()) {
            proxy = (Factory) createInitializedOrUninitializedInstanceOfType(enhancedClass);
        } else {
            proxy = (Factory) createUninitializedInstanceOfType(enhancedClass);
        }
        proxy.setCallbacks(new Callback[]{new CglibProxyMethodInterceptor<T>(name, proxiedClass, invocationHandler, this, cloneService)});
        return (T) proxy;
    }

    /**
     * Creates an instance of the given type. First we try to create an instance using the default constructor.
     * If this doesn't work, eg if there is no default constructor, we try using objenesis. This way the class doesn't
     * have to offer an empty constructor in order for this method to succeed.
     *
     * @param <T>   The type of the instance
     * @param clazz The class for which an instance is requested, not null
     * @return An instance of the given class, not null
     */
    @SuppressWarnings("unchecked")
    public <T> T createInitializedOrUninitializedInstanceOfType(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new UnitilsException("Cannot create instance of an interface type: " + clazz);
        }
        try {
            return createInstanceOfType(clazz, true);
        } catch (UnitilsException e) {
            logger.warn("Could not create initialized instance of type " + clazz.getSimpleName() + ". No no-arg constructor found. All fields in the instance will have the java default values. Add a default constructor (can be private) if the fields should be initialized. If this concerns an inner class, make sure it is declared static. Partial mocking of non-static inner classes is not supported.");
        }
        // unable to create type using regular constructor, try objenesis
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
    public <T> T createUninitializedInstanceOfType(Class<T> clazz) {
        if (clazz.isInterface() || isAbstract(clazz.getModifiers())) {
            throw new UnitilsException("Cannot create instance of an interface or abstract type: " + clazz);
        }
        Objenesis objenesis = new ObjenesisStd();
        return objenesis.newInstance(clazz);
    }

    /**
     * @param object The object to check
     * @return The proxied type, null if the object is not a proxy
     */
    public Class<?> getProxiedTypeIfProxy(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Factory) {
            Callback[] callbacks = ((Factory) object).getCallbacks();
            if (callbacks != null && callbacks.length == 1 && callbacks[0] instanceof CglibProxyMethodInterceptor) {
                return ((CglibProxyMethodInterceptor) callbacks[0]).getProxiedType();
            }
        }
        return null;
    }

    /**
     * First finds a trace element in which a cglib proxy method was invoked. Then it returns the rest of the stack trace following that
     * element. The stack trace starts with the element is the method call that was proxied by the proxy method.
     *
     * @return The proxied method trace, not null
     */
    public StackTraceElement[] getProxiedMethodStackTrace(StackTraceElement[] stackTrace) {
        List<StackTraceElement> result = new ArrayList<StackTraceElement>();

        boolean foundProxyMethod = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (foundProxyMethod) {
                result.add(stackTraceElement);

            } else if (isCglibProxyClassName(stackTraceElement.getClassName())) {
                // found the proxy method element, the next element is the proxied method element
                foundProxyMethod = true;
            }
        }
        if (result.isEmpty()) {
            throw new UnitilsException("No invocation of a cglib proxy method found in stack trace: " + Arrays.toString(stackTrace));
        }
        return result.toArray(new StackTraceElement[result.size()]);
    }


    @SuppressWarnings("unchecked")
    protected <T> Class<T> createEnhancedClass(Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        Enhancer enhancer = new Enhancer();

        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        interfaces.add(Cloneable.class);
        interfaces.add(ObjectToFormat.class);
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