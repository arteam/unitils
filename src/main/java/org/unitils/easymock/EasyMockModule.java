package org.unitils.easymock;

import org.easymock.classextension.internal.ClassExtensionHelper;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsModule;
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
 * Mocks can also be created explicitly by using the {@link EasyMockModule#createMock(Class)} method.
 * <p/>
 * Switching to the replayAll state and verifying expectations of all mocks (including the mocks created with
 * the {@link EasyMockModule#createMock(Class)} method can be done by calling the {@link EasyMockModule#replayAll()} and
 * {@link EasyMockModule#verifyAll()} methods.
 */
public class EasyMockModule implements UnitilsModule {


    //todo refactor
    private static EasyMockModule instance = new EasyMockModule();

    //todo refactor
    public static EasyMockModule getInstance() {
        return instance;
    }


    public static <T> T createMock(Class<T> mockType) {

        //todo implement
        return null;
    }

    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having set their expectations on the mock objects.
     * <p/>
     * This method will make sure EasyMock's replayAll method is called on every mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the {@link #createMock(Class)} method
     */
    public static void replayAll() {

        getInstance().replayAllImpl();

    }


    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having executed the tested method on the object under test. This method
     * will make sure EasyMock's verifyAll method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    public static void verifyAll() {

        getInstance().verifyAllImpl();
    }


    //todo javadoc
    public TestListener createTestListener() {

        return new EasyMockTestListener();
    }


    //todo javadoc
    protected void replayAllImpl() {

        List<MocksControl> mockControls = getAllMockControls(TestContext.getTestObject());
        for (MocksControl mocksControl : mockControls) {
            mocksControl.replay();
        }
    }


    //todo javadoc
    public void verifyAllImpl() {

        List<MocksControl> mockControls = getAllMockControls(TestContext.getTestObject());
        for (MocksControl mocksControl : mockControls) {
            mocksControl.verify();
        }
    }

    /**
     * todo javadoc
     *
     * @param testObject
     */
    protected static List<MocksControl> getAllMockControls(Object testObject) {

        List<MocksControl> result = new ArrayList<MocksControl>();

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field field : fields) {

            Object mockObject = ReflectionUtils.getFieldValue(testObject, field);
            MocksControl mocksControl = ClassExtensionHelper.getControl(mockObject);
            if (mocksControl != null) {
                result.add(mocksControl);
            }
        }
        return result;
    }


    /**
     * Creates and sets a mock for all {@link @Mock} annotated fields.
     * <p/>
     * The {@link #createMock(Class, Mock)} method is called for creating the mocks. Ones the mock is created,
     * all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectMocksIntoTest(Object testObject) {

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field field : fields) {

            Class<?> mockType = field.getType();
            Object mockObject = createMock(mockType, field.getAnnotation(Mock.class));
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

            } catch (RuntimeException e) {

                throw new RuntimeException("Unable to invoke after create mock method. Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }

    /**
     * Creates an EasyMock mock instance of the given type (class/interface). The type of mock is determined
     * by the arguments of the annotation:
     * <p/>
     * If returns is set to NICE, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enable
     *
     * @param type           the class/interface, not null
     * @param mockAnnotation the mock annotation, not null
     * @return a mock for the given class or interface, not null
     */
    protected <T> T createMock(Class<T> type, Mock mockAnnotation) {

        // Get anotation arguments and replace default values if needed
        Mock.Order order = AnnotationUtils.getValueReplaceDefault(mockAnnotation.order());
        Mock.Returns returns = AnnotationUtils.getValueReplaceDefault(mockAnnotation.returns());
        Mock.Arguments arguments = AnnotationUtils.getValueReplaceDefault(mockAnnotation.arguments());

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

        return mocksControl.createMock(type);
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

            createAndInjectMocksIntoTest(TestContext.getTestObject());
        }
    }

}
