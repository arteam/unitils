/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.mock;

import junit.framework.TestCase;
import org.easymock.classextension.internal.ClassExtensionHelper;

import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;

import static org.easymock.classextension.EasyMock.*;

/**
 * Base class for testing with mock objects using EasyMock.
 */
public class EasyMockTestCase extends TestCase {

    private Set mocks = new HashSet();

    protected void setUp() throws Exception {
        injectMocksIntoTest();
    }

    protected <T> T getMock(Class<T> clazz) {
        T mock = createMock(clazz);
        registerMock(mock);
        injectMock(clazz, mock);
        return mock;
    }

    protected void injectMock(Class clazz, Object mock) {
    }

    private void registerMock(Object mock) {
        mocks.add(mock);
    }

    protected void replay() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).replay();
        }
    }

    protected void verify() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).verify();
        }
    }

    private void injectMocksIntoTest() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Mock.class) != null) {
                field.setAccessible(true);
                Object mock = getMock(field.getType());
                field.set(this, mock);
            }
        }
    }

}
