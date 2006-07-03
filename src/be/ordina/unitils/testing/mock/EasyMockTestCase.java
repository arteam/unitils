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
 * <p>
 * Mock creation is simplified by automatically inserting EasyMock generated mocks for fields annotated with the
 * {@link @Mock} annotation. A hook method is foreseen (<code>injectMock</code>) for injecting mock objects in the
 * tested objects immediately after they are created.
 * <p>
 * Switching mocks to the replay state and verifying expectations on mocks is simplified by the methods <code>replay()
 * </code> and <code>verify</code>, that call replay/verify on all mocks that are used in the test at once.
 *
 */
public class EasyMockTestCase extends TestCase {

    /* All mocks that are used by this test are held in this Set */
    private Set mocks = new HashSet();

    /**
     * Setup method. Makes sure a EasyMock generated mock is supplied to the fields annotated with @Mock
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        injectMocksIntoTest();
    }

    /**
     * Returns an EasyMock generated mock for the given class or interface
     * @param clazz
     * @return A mock for the given class or interface
     */
    protected <T> T getMock(Class<T> clazz) {
        T mock = createMock(clazz);
        mocks.add(mock);
        injectMock(clazz, mock);
        return mock;
    }

    /**
     * Override this method to inject the mock objects that are supplied to the fields annotated with {@link @Mock}, or
     * directly created by the <code>getMock</code> method, in your tested object(s).
     * @param clazz
     * @param mock
     */
    protected void injectMock(Class clazz, Object mock) {
    }

    /**
     * Unit tests should call this method after having set their expectations on the mock objects. This method will
     * make sure EasyMock's replay method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    protected void replay() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).replay();
        }
    }

    /**
     * Unit tests should call this method after having executed the tested method on the object under test. This method
     * will make sure EasyMock's verify method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    protected void verify() {
        for (Object mock : mocks) {
            ClassExtensionHelper.getControl(mock).verify();
        }
    }

    /**
     * Makes sure a EasyMock generated mock is supplied to all the fields annotated with {@link @Mock}. The <code>
     * injectMock</code> method is called for every mock to enable the implementing class to inject the mock objects
     * into the tested object(s).
     * @throws IllegalAccessException
     */
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
