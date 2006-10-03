package org.unitils.easymock;

import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.unitils.core.*;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.annotation.Mock;
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
 * Mocks can also be created explicitly by using the {@link #createMock(Class, Mock.Order, Mock.Returns, Mock.Arguments)} method.
 * <p/>
 * Switching to the replayAll state and verifying expectations of all mocks (including the mocks created with
 * the {@link #createMock(Class, Mock.Order, Mock.Returns, Mock.Arguments)} method can be done by calling
 * the {@link EasyMockModule#replay()} and {@link EasyMockModule#verify()} methods.
 */
public class EasyMockModule implements UnitilsModule {

    private List<MocksControl> mocksControls;


    public EasyMockModule() {
        this.mocksControls = new ArrayList<MocksControl>();
    }


    public static <T> T createMock(Class<T> mockType, Mock.Order order, Mock.Returns returns, Mock.Arguments arguments) {

        return getInstance().createMockImpl(mockType, order, returns, arguments);
    }

    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having set their expectations on the mock objects.
     * <p/>
     * This method will make sure EasyMock's replayAll method is called on every mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the {@link #createMock(Class, Mock.Order, Mock.Returns, Mock.Arguments)} method
     */
    public static void replay() {

        getInstance().replayImpl();
    }


    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having executed the tested method on the object under test. This method
     * will make sure EasyMock's verifyAll method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    public static void verify() {

        getInstance().verifyImpl();
    }


    //todo javadoc
    public TestListener createTestListener() {

        return new EasyMockTestListener();
    }


    //todo javadoc
    protected void replayImpl() {

        for (MocksControl mocksControl : mocksControls) {
            mocksControl.replay();
        }
    }


    //todo javadoc
    protected void verifyImpl() {

        for (MocksControl mocksControl : mocksControls) {
            mocksControl.verify();
        }
    }


    /**
     * Creates and sets a mock for all {@link @Mock} annotated fields.
     * <p/>
     * The {@link #createMockImpl(Class, Mock.Order, Mock.Returns, Mock.Arguments)} method is called for creating the
     * mocks. Ones the mock is created, all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectMocksIntoTest(Object testObject) {

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field field : fields) {

            Class<?> mockType = field.getType();

            Mock mockAnnotation = field.getAnnotation(Mock.class);
            Object mockObject = createMock(mockType, mockAnnotation.order(), mockAnnotation.returns(), mockAnnotation.arguments());
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

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)", e);
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
    protected <T> T createMockImpl(Class<T> mockType, Mock.Order order, Mock.Returns returns, Mock.Arguments arguments) {

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
    protected MocksControl createMocksControl(Class type, Mock.Order order, Mock.Returns returns, Mock.Arguments arguments) {

        // Get anotation arguments and replace default values if needed
        order = AnnotationUtils.getValueReplaceDefault(order);
        returns = AnnotationUtils.getValueReplaceDefault(returns);
        arguments = AnnotationUtils.getValueReplaceDefault(arguments);

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
            mocksControl = new MocksControl(mockType);
        }

        // Check order
        if (Mock.Order.STRICT == order) {
            mocksControl.checkOrder(true);
        }

        return mocksControl;
    }

    //todo refactor
    private static EasyMockModule getInstance() {

        ModulesRepository modulesRepository = Unitils.getInstance().getModulesRepositoryImpl();
        EasyMockModule module = modulesRepository.getModule(EasyMockModule.class);
        if (module == null) {
            //todo
            throw new UnitilsException("todo");
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
        public void beforeTestMethod() {

            TestContext testContext = Unitils.getTestContext();
            createAndInjectMocksIntoTest(testContext.getTestObject());
        }

        public void afterTestMethod() {
            verify();
        }
    }

}
