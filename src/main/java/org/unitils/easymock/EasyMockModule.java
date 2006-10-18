package org.unitils.easymock;

import org.apache.commons.configuration.Configuration;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.annotation.Mock.Arguments;
import org.unitils.easymock.annotation.Mock.Order;
import org.unitils.easymock.annotation.Mock.Returns;
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
 * Mocks can also be created explicitly by using the {@link #createMock(Class,Mock.Order,Mock.Returns,Mock.Arguments)} method.
 * <p/>
 * Switching to the replay state and verifying expectations of all mocks (including the mocks created with
 * the {@link #createMock(Class,Mock.Order,Mock.Returns,Mock.Arguments)} method can be done by calling
 * the {@link EasyMockModule#replay()} and {@link EasyMockModule#verify()} methods.
 */
public class EasyMockModule implements UnitilsModule {

    /* All created mocks controls */
    private List<MocksControl> mocksControls;

    //todo javadoc
    private Order defaultOrder;

    private Returns defaultReturns;

    private Arguments defaultArguments;

    /**
     * Initializes the module
     */
    public void init(Configuration configuration) {
        this.mocksControls = new ArrayList<MocksControl>();

        defaultOrder = ReflectionUtils.getEnumValue(Order.class, configuration.getString(Order.class.getName()));
        defaultReturns = ReflectionUtils.getEnumValue(Returns.class, configuration.getString(Returns.class.getName()));
        defaultArguments = ReflectionUtils.getEnumValue(Arguments.class, configuration.getString(Arguments.class.getName()));
    }


    /**
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     *
     * @param mockType  the class type for the mock, not null
     * @param order     the order setting, not null
     * @param returns   the returns setting, not null
     * @param arguments the arguments setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(Class<T> mockType, Mock.Order order, Mock.Returns returns, Mock.Arguments arguments) {

        return getInstance().createMockImpl(mockType, order, returns, arguments);
    }

    /**
     * Unit tests should call this method after having set their recorded expected behavior on the mock objects.
     * <p/>
     * This method will make sure EasyMock's replay method is called on every mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the
     * {@link #createMock(Class,Mock.Order,Mock.Returns,Mock.Arguments)} method.
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
     * {@link #createMock(Class,Mock.Order,Mock.Returns,Mock.Arguments)} method.
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
     * Creates and sets a mock for all {@link @Mock} annotated fields.
     * <p/>
     * The {@link #createMockImpl(Class,Mock.Order,Mock.Returns,Mock.Arguments)} method is called for creating the
     * mocks. Ones the mock is created, all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectMocksIntoTest(Object testObject) {

        mocksControls = new ArrayList<MocksControl>();

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field field : fields) {

            Class<?> mockType = field.getType();

            Mock mockAnnotation = field.getAnnotation(Mock.class);
            Object mockObject = createMockImpl(mockType, mockAnnotation.order(), mockAnnotation.returns(), mockAnnotation.arguments());
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
     * @param mockType  the class type for the mock, not null
     * @param order     the order setting, not null
     * @param returns   the returns setting, not null
     * @param arguments the arguments setting, not null
     * @return a mock for the given class or interface, not null
     */
    protected <T> T createMockImpl(Class<T> mockType, Order order, Returns returns, Arguments arguments) {

        MocksControl mocksControl = createMocksControl(mockType, order, returns, arguments);
        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }

    /**
     * Creates an EasyMock mock instance of the given type (class/interface). The type of mock is determined
     * as follows:
     * <p/>
     * If returns is set to NICE, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enabled
     *
     * @param type      the class/interface, not null
     * @param order     the order setting, not null
     * @param returns   the returns setting, not null
     * @param arguments the arguments setting, not null
     * @return a mockcontrol for the given class or interface, not null
     */
    protected MocksControl createMocksControl(Class type, Order order, Returns returns, Arguments arguments) {

        // Get anotation arguments and replace default values if needed
        order = ReflectionUtils.getValueReplaceDefault(order, defaultOrder);
        returns = ReflectionUtils.getValueReplaceDefault(returns, defaultReturns);
        arguments = ReflectionUtils.getValueReplaceDefault(arguments, defaultArguments);

        // Check returns
        MocksControl.MockType mockType = DEFAULT;
        if (Mock.Returns.NICE == returns) {
            mockType = NICE;

        } else if (Mock.Returns.STRICT == returns) {
            mockType = DEFAULT;
        }

        // Check arguments
        MocksControl mocksControl;
        if (Mock.Arguments.LENIENT == arguments) {
            mocksControl = new LenientMocksControl(mockType, IGNORE_DEFAULTS, LENIENT_DATES, LENIENT_ORDER);

        } else {
            mocksControl = new MocksClassControl(mockType);
        }

        // Check order
        if (Mock.Order.STRICT == order) {
            mocksControl.checkOrder(true);
        }

        return mocksControl;
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
