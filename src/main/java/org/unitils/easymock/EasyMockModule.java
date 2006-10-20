package org.unitils.easymock;

import org.apache.commons.configuration.Configuration;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.Module;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.annotation.LenientMock;
import org.unitils.easymock.annotation.Mock;
import org.unitils.reflectionassert.ReflectionComparatorModes;
import static org.unitils.reflectionassert.ReflectionComparatorModes.*;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Module for testing with mock objects using EasyMock.
 * <p/>
 * Mock creation is simplified by automatically inserting EasyMock generated mocks for fields annotated with the
 * {@link @Mock} annotation.
 * <p/>
 * All methods annotated with {@link @AfterCreateMock} will be called when a mock object was created. This provides
 * you with a hook method for custom handling of the mock (e.g. adding the mocks to a service locator repository).
 * A method can only be called if it has following signature <code>void myMethod(Object mock, String name, Class type)</code>.
 * <p/>
 * Mocks can also be created explicitly by using the {@link #createMock(Class,Mock.InvocationOrder,Mock.Returns)} and
 * {@link #createLenientMock(Class,LenientMock.InvocationOrder,LenientMock.Returns,LenientMock.Order,LenientMock.Dates,LenientMock.Defaults)} methods.
 * <p/>
 * Switching to the replay state and verifying expectations of all mocks (including the mocks created with
 * the createMock() method can be done by calling
 * the {@link EasyMockModule#replay()} and {@link EasyMockModule#verify()} methods.
 */
public class EasyMockModule implements Module {

    /* All created mocks controls */
    private List<MocksControl> mocksControls;

    //todo javadoc
    private Mock.InvocationOrder defaultMockInvocationOrder;

    private Mock.Returns defaultMockReturns;

    private LenientMock.InvocationOrder defaultLenientMockInvocationOrder;

    private LenientMock.Returns defaultLenientMockReturns;

    private LenientMock.Order defaultLenientMockOrder;

    private LenientMock.Dates defaultLenientMockDates;

    private LenientMock.Defaults defaultLenientMockDefaults;


    /**
     * Initializes the module
     */
    public void init(Configuration configuration) {
        this.mocksControls = new ArrayList<MocksControl>();

        defaultMockInvocationOrder = ReflectionUtils.getEnumValue(Mock.InvocationOrder.class, configuration.getString(Mock.InvocationOrder.class.getName()));
        defaultMockReturns = ReflectionUtils.getEnumValue(Mock.Returns.class, configuration.getString(Mock.Returns.class.getName()));
        defaultLenientMockInvocationOrder = ReflectionUtils.getEnumValue(LenientMock.InvocationOrder.class, configuration.getString(LenientMock.InvocationOrder.class.getName()));
        defaultLenientMockReturns = ReflectionUtils.getEnumValue(LenientMock.Returns.class, configuration.getString(LenientMock.Returns.class.getName()));
        defaultLenientMockOrder = ReflectionUtils.getEnumValue(LenientMock.Order.class, configuration.getString(LenientMock.Order.class.getName()));
        defaultLenientMockDates = ReflectionUtils.getEnumValue(LenientMock.Dates.class, configuration.getString(LenientMock.Dates.class.getName()));
        defaultLenientMockDefaults = ReflectionUtils.getEnumValue(LenientMock.Defaults.class, configuration.getString(LenientMock.Defaults.class.getName()));
    }


    /**
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     *
     * @param mockType the class type for the mock, not null
     * @param order    the order setting, not null
     * @param returns  the returns setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(Class<T> mockType, Mock.InvocationOrder order, Mock.Returns returns) {

        return getInstance().createMockImpl(mockType, order, returns);
    }


    //todo javadoc
    public static <T> T createLenientMock(Class<T> mockType, LenientMock.InvocationOrder invocationOrder, LenientMock.Returns returns,
                                          LenientMock.Order order, LenientMock.Dates dates, LenientMock.Defaults defaults) {

        return getInstance().createLenientMockImpl(mockType, invocationOrder, returns, order, dates, defaults);
    }

    /**
     * Unit tests should call this method after having set their recorded expected behavior on the mock objects.
     * <p/>
     * This method will make sure EasyMock's replay method is called on every mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the
     * {@link #createMock(Class,Mock.InvocationOrder,Mock.Returns)} and
     * {@link #createLenientMock(Class,LenientMock.InvocationOrder,LenientMock.Returns,LenientMock.Order,LenientMock.Dates,LenientMock.Defaults)} methods.
     * <p/>
     * After each test, the expected behavior will be verified automatically. Verification can also be performed
     * explicitly by calling the {@link #verify()} method.
     */
    public static void replay() {

        getInstance().replayImpl();
    }


    /**
     * Unit tests should call this method to check whether all recorded expected behavior was actually observed during
     * the test.
     * <p/>
     * This method will make sure EasyMock's verify method is called on every mock mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the
     * {@link #createMock(Class,Mock.InvocationOrder,Mock.Returns)} and
     * {@link #createLenientMock(Class,LenientMock.InvocationOrder,LenientMock.Returns,LenientMock.Order,LenientMock.Dates,LenientMock.Defaults)} methods.
     * <p/>
     * After each test, the expected behavior will be verified automatically. Verification can also be performed
     * explicitly by calling this method.
     */
    public static void verify() {

        getInstance().verifyImpl();
    }


    /**
     * Creates the listener for plugging in the behavior of this module into the test runs.
     *
     * @return the listener
     */
    public TestListener createTestListener() {

        return new EasyMockTestListener();
    }


    /**
     * Implements the setting of the recorded behavior. See {@link #replay()}.
     */
    protected void replayImpl() {

        for (MocksControl mocksControl : mocksControls) {
            mocksControl.replay();
        }
    }


    /**
     * Implements the verification of the recorded behavior. See {@link #verify()}.
     */
    protected void verifyImpl() {

        for (MocksControl mocksControl : mocksControls) {
            mocksControl.verify();
        }
    }


    /**
     * Creates and sets a mock for all {@link @Mock} and {@link @LenientMock} annotated fields.
     * <p/>
     * The {@link #createMock(Class,Mock.InvocationOrder,Mock.Returns)} or
     * {@link #createLenientMock(Class,LenientMock.InvocationOrder,LenientMock.Returns,LenientMock.Order,LenientMock.Dates,LenientMock.Defaults)}
     * method is called for creating the mocks. Ones the mock is created, all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectMocksIntoTest(Object testObject) {

        mocksControls = new ArrayList<MocksControl>();

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field field : fields) {

            Class<?> mockType = field.getType();

            Mock mockAnnotation = field.getAnnotation(Mock.class);
            //todo lenient mock
            Object mockObject = createMockImpl(mockType, mockAnnotation.invocationOrder(), mockAnnotation.returns());
            ReflectionUtils.setFieldValue(testObject, field, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, field.getName(), mockType);
        }
    }

    /**
     * Calls all {@link @AfterCreateMock} annotated methods on the test, passing the given mock.
     * These annotated methods must have following signature <code>void myMethod(Object mock, String name, Class type)</code>.
     * If this is not the case, a runtime exception is called.
     *
     * @param testObject the test, not null
     * @param mockObject the mock, not null
     * @param name       the field(=mock) name, not null
     * @param type       the field(=mock) type
     */
    protected void callAfterCreateMockMethods(Object testObject, Object mockObject, String name, Class type) {

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, mockObject, name, type);
            } catch (Exception e) {
                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: " +
                        "void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }

    /**
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     *
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param returns         the returns setting, not null
     * @return a mock for the given class or interface, not null
     */
    protected <T> T createMockImpl(Class<T> mockType, Mock.InvocationOrder invocationOrder, Mock.Returns returns) {

        // Get anotation arguments and replace default values if needed
        invocationOrder = ReflectionUtils.getValueReplaceDefault(invocationOrder, defaultMockInvocationOrder);
        returns = ReflectionUtils.getValueReplaceDefault(returns, defaultMockReturns);

        MocksControl mocksControl;
        if (Mock.Returns.NICE == returns) {
            mocksControl = new MocksClassControl(NICE);

        } else {
            mocksControl = new MocksClassControl(DEFAULT);
        }

        // Check order
        if (Mock.InvocationOrder.STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }

        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }


    /**
     * todo javadoc
     * <p/>
     * Creates an EasyMock mock instance of the given type (class/interface). The type of mock is determined
     * as follows:
     * <p/>
     * If returns is set to NICE, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enabled
     *
     * @param mockType        the class/interface, not null
     * @param invocationOrder the order setting, not null
     * @param returns         the returns setting, not null
     * @param order           todo
     * @param dates           todo
     * @param defaults        todo
     * @return a mockcontrol for the given class or interface, not null
     */
    protected <T> T createLenientMockImpl(Class<T> mockType, LenientMock.InvocationOrder invocationOrder, LenientMock.Returns returns,
                                          LenientMock.Order order, LenientMock.Dates dates, LenientMock.Defaults defaults) {

        // Get anotation arguments and replace default values if needed
        invocationOrder = ReflectionUtils.getValueReplaceDefault(invocationOrder, defaultLenientMockInvocationOrder);
        returns = ReflectionUtils.getValueReplaceDefault(returns, defaultLenientMockReturns);
        order = ReflectionUtils.getValueReplaceDefault(order, defaultLenientMockOrder);
        dates = ReflectionUtils.getValueReplaceDefault(dates, defaultLenientMockDates);
        defaults = ReflectionUtils.getValueReplaceDefault(defaults, defaultLenientMockDefaults);

        List<ReflectionComparatorModes> comparatorModes = new ArrayList<ReflectionComparatorModes>();
        if (LenientMock.Order.LENIENT == order) {
            comparatorModes.add(LENIENT_ORDER);
        }
        if (LenientMock.Dates.LENIENT == dates) {
            comparatorModes.add(LENIENT_DATES);
        }
        if (LenientMock.Defaults.IGNORE_DEFAULTS == defaults) {
            comparatorModes.add(IGNORE_DEFAULTS);
        }

        LenientMocksControl mocksControl;
        if (LenientMock.Returns.NICE == returns) {
            mocksControl = new LenientMocksControl(NICE, comparatorModes.toArray(new ReflectionComparatorModes[0]));

        } else {
            mocksControl = new LenientMocksControl(DEFAULT, comparatorModes.toArray(new ReflectionComparatorModes[0]));
        }

        // Check order
        if (LenientMock.InvocationOrder.STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }

        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }


    /**
     * Gets the first instance of an EasyMockModule that is stored in the modules repository.
     * This instance implements the actual behavior of the static methods, such as {@link #replay()}.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     * @throws UnitilsException when no such module could be found
     */
    private static EasyMockModule getInstance() {

        EasyMockModule module = Unitils.getModulesRepository().getFirstModule(EasyMockModule.class);
        if (module == null) {

            throw new UnitilsException("Unable to find an instance of an EasyMockModule in the modules repository.");
        }
        return module;
    }


    /**
     * Test listener that handles the mock creation and injection.
     */
    private class EasyMockTestListener extends TestListener {

        /**
         * Before the test is executed this calls {@link EasyMockModule#createAndInjectMocksIntoTest(Object)} to
         * create and inject all mocks on the class.
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {

            // Clear all previously created mocks controls
            mocksControls.clear();

            createAndInjectMocksIntoTest(testObject);
        }

        /**
         * After each test is executed this calls {@link EasyMockModule#verifyImpl()} to verify the recorded behavior
         * of all created mocks.
         */
        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {

            verifyImpl();
        }
    }

}

