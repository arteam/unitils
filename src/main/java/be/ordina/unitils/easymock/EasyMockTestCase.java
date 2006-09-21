/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.easymock;

import be.ordina.unitils.inject.AutoInjector;
import be.ordina.unitils.util.UnitilsConfiguration;
import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.createMock;
import org.easymock.classextension.internal.ClassExtensionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base class for testing with mock objects using EasyMock.
 * <p/>
 * Mock creation is simplified by automatically inserting EasyMock generated mocks for fields annotated with the
 * {@link @Mock} annotation. A hook method is foreseen (<code>injectMock</code>) for injecting mock objects in the
 * tested objects immediately after they are created.
 * <p/>
 * Switching mocks to the replay state and verifying expectations on mocks is simplified by the methods <code>replay()
 * </code> and <code>verify</code>, that call replay/verify on all mocks that are used in the test at once.
 */
public class EasyMockTestCase extends TestCase {

    /* All mocks that are used by this test are held in this Set */
    private Map<String, Object> mocks = new HashMap<String, Object>();

    /* Implementation of AutoInjector for automatically injecting mocks into objects */
    private static AutoInjector autoInjector;

    /* The configuration (unittest.properties) */
    protected static Properties properties;

    /* Property key for the default mode of auto injection that is used */
    private static final String PROPKEY_AUTOINJECTION_DEAULTMODE = "mocks.autoinjection.defaultMode";

    /* Possible values for the property key 'mocks.autoinjection.defaultMode' */
    private static final String AUTOINJECTIONMODE_NOINJECTION = "no";
    private static final String AUTOINJECTIONMODE_BYNAME = "byName";
    private static final String AUTOINJECTIONMODE_BYTYPE = "byType";
    private static final String AUTOINJECTIONMODE_CONSTRUCTOR = "constructor";
    private static final String AUTOINJECTIONMODE_AUTODETECT = "autodetect";

    /**
     * Setup method. Makes sure a EasyMock generated mock is supplied to the fields annotated with @Mock
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (properties == null) {
            properties = UnitilsConfiguration.loadProperties(UnitilsConfiguration.DEFAULT_PROPERTIES_FILE_NAME);
        }
        injectMocksIntoTest();
    }

    /**
     * Returns an EasyMock generated mock for the given class or interface
     *
     * @param clazz
     * @return A mock for the given class or interface
     */
    protected <T> T getMock(Class<T> clazz) {
        return getMock(String.valueOf(mocks.size()), clazz);
    }

    /**
     * Returns an EasyMock generated mock for the given class or interface
     *
     * @param name  A name for the mock object, which is used when autoInjecting mocks into objects by name
     * @param clazz
     * @return A mock for the given class or interface
     */
    protected <T> T getMock(String name, Class<T> clazz) {
        T mock = createMock(clazz);
        mocks.put(name, mock);
        injectMock(clazz, mock);
        return mock;
    }

    /**
     * Override this method to inject the mock objects that are supplied to the fields annotated with {@link @Mock}, or
     * directly created by the <code>getMock</code> method, in your tested object(s). If you use the ServiceLocator
     * pattern for resolving dependencies, you can override this method for injecting the mocks into your ServiceLocator.
     *
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
        for (Object mock : mocks.values()) {
            ClassExtensionHelper.getControl(mock).replay();
        }
    }

    /**
     * Unit tests should call this method after having executed the tested method on the object under test. This method
     * will make sure EasyMock's verify method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    protected void verify() {
        for (Object mock : mocks.values()) {
            ClassExtensionHelper.getControl(mock).verify();
        }
    }

    /**
     * Makes sure a EasyMock generated mock is supplied to all the fields annotated with {@link @Mock}. The <code>
     * injectMock</code> method is called for every mock to enable the implementing class to inject the mock objects
     * into the tested object(s).
     *
     * @throws IllegalAccessException
     */
    private void injectMocksIntoTest() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Mock.class) != null) {
                field.setAccessible(true);
                Object mock = getMock(field.getName(), field.getType());
                field.set(this, mock);
            }
        }
    }

}
