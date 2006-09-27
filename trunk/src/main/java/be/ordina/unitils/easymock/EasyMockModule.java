package be.ordina.unitils.easymock;

import be.ordina.unitils.easymock.annotation.Mock;
import be.ordina.unitils.module.TestContext;
import be.ordina.unitils.module.TestListener;
import be.ordina.unitils.module.UnitilsModule;
import static be.ordina.unitils.reflectionassert.ReflectionComparatorModes.*;
import be.ordina.unitils.util.AnnotationUtils;
import org.easymock.classextension.internal.ClassExtensionHelper;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * todo javadoc
 * <p/>
 * * Base class for testing with mock objects using EasyMock.
 * <p/>
 * Mock creation is simplified by automatically inserting EasyMock generated mocks for fields annotated with the
 * {@link @Mock} annotation. A hook method is foreseen (<code>injectMock</code>) for injecting mock objects in the
 * tested objects immediately after they are created.
 * <p/>
 * Switching mocks to the replay state and verifying expectations on mocks is simplified by the methods <code>replay()
 * </code> and <code>verify</code>, that call replay/verify on all mocks that are used in the test at once.
 */
public class EasyMockModule implements UnitilsModule {

    //todo ook zelf mocks kunnen aanmaken en replayen ...


    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having set their expectations on the mock objects. This method will
     * make sure EasyMock's replay method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    public static void replay() {

        //todo none static implementation
        try {
            List<MocksControl> mockControls = getAllMockControls(TestContext.getTestObject());
            for (MocksControl mocksControl : mockControls) {
                mocksControl.replay();
            }
        } catch (IllegalAccessException e) {
            //Todo implement
            e.printStackTrace();
        }

    }

    /**
     * todo javadoc
     * <p/>
     * Unit tests should call this method after having executed the tested method on the object under test. This method
     * will make sure EasyMock's verify method is called on every mock object that was supplied to the fields annotated
     * with {@link @Mock}, or directly created by the <code>getMock</code> method
     */
    public static void verify() {

        try {
            List<MocksControl> mockControls = getAllMockControls(TestContext.getTestObject());
            for (MocksControl mocksControl : mockControls) {
                mocksControl.verify();
            }
        } catch (IllegalAccessException e) {
            //Todo implement
            e.printStackTrace();
        }
    }


    //todo javadoc
    public TestListener createTestListener() {

        return new EasyMockTestListener();
    }

    /**
     * todo javadoc
     *
     * @param testObject
     * @return
     * @throws IllegalAccessException
     */
    protected static List<MocksControl> getAllMockControls(Object testObject) throws IllegalAccessException {

        List<MocksControl> result = new ArrayList<MocksControl>();

        Field[] fields = testObject.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (field.isAnnotationPresent(Mock.class)) {

                field.setAccessible(true);
                Object mockObject = field.get(testObject);
                MocksControl mocksControl = ClassExtensionHelper.getControl(mockObject);
                if (mocksControl != null) {
                    result.add(mocksControl);
                }
            }
        }
        return result;
    }

    /**
     * Makes sure a EasyMock generated mock is supplied to all the fields annotated with {@link @Mock}. The <code>
     * injectMock</code> method is called for every mock to enable the implementing class to inject the mock objects
     * into the tested object(s).
     *
     * @throws IllegalAccessException
     */
    protected void injectMocksIntoTest(Object testObject) throws IllegalAccessException {

        Field[] fields = testObject.getClass().getDeclaredFields();
        for (Field field : fields) {

            Mock mockAnnotation = field.getAnnotation(Mock.class);
            if (field.getAnnotation(Mock.class) != null) {

                field.setAccessible(true);
                Object mockObject = createMock(field.getType(), mockAnnotation);
                field.set(testObject, mockObject);
            }
        }
    }


    /**
     * todo javadoc
     * <p/>
     * Returns an EasyMock generated mock for the given class or interface
     *
     * @return A mock for the given class or interface
     */
    protected <T> T createMock(Class<T> type, Mock mockAnnotation) {

        Mock.Order order = AnnotationUtils.getValueReplaceDefault(mockAnnotation.order());
        Mock.Returns returns = AnnotationUtils.getValueReplaceDefault(mockAnnotation.returns());
        Mock.Arguments arguments = AnnotationUtils.getValueReplaceDefault(mockAnnotation.arguments());

        MocksControl.MockType mockType = DEFAULT;
        if (Mock.Returns.Nice == returns) {
            mockType = NICE;

        } else if (Mock.Returns.Strict == returns) {
            mockType = DEFAULT;
        }

        MocksControl mocksControl;
        if (Mock.Arguments.Lenient == arguments) {
            mocksControl = new LenientMocksControl(mockType, IGNORE_DEFAULTS, LENIENT_DATES, LENIENT_ORDER);

        } else {
            mocksControl = new MocksControl(mockType);
        }

        if (Mock.Order.Strict == order) {
            mocksControl.checkOrder(true);
        }

        return mocksControl.createMock(type);
    }


    private class EasyMockTestListener extends TestListener {
        /**
         * todo javadoc
         * <p/>
         * Setup method. Makes sure a EasyMock generated mock is supplied to the fields annotated with @Mock
         */
        public void beforeTestMethod() {

            //Todo refactor
            try {
                injectMocksIntoTest(TestContext.getTestObject());
            } catch (IllegalAccessException e) {

                //todo implement
                throw new RuntimeException(e);
            }
        }
    }

}
