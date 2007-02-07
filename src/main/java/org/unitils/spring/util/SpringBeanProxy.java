package org.unitils.spring.util;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Proxy that wraps a spring bean. Offers a getter and setter that enables wrapping the proxied instance, replacing it
 * with another instance or restoring the original instance at any point in time, after instantiation of spring's
 * <code>ApplicationContext</code>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringBeanProxy implements MethodInterceptor {

    /* The spring bean that is proxied, or an object of same type that wraps or replaces the original spring bean */
    private Object proxiedSpringBean;

    /**
     * Creates a new instance wrapping the given springBean.
     * @param proxiedSpringBean The spring bean that is proxied
     */
    public SpringBeanProxy(Object proxiedSpringBean) {
        this.proxiedSpringBean = proxiedSpringBean;
    }

    /**
     * This method is invoked when a method is called on the instance. Simply passes through to the wrapped instance.
     *
     * @param object
     * @param method
     * @param args
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invokeSuper(proxiedSpringBean, args);
    }

    /**
     * @return The spring bean, wrapper or replacement object that is proxied
     */
    public Object getProxiedSpringBean() {
        return proxiedSpringBean;
    }

    /**
     * Switches the current proxied object with a different one.
     * @param proxiedSpringBean
     */
    public void setProxiedSpringBean(Object proxiedSpringBean) {
        this.proxiedSpringBean = proxiedSpringBean;
    }

}
