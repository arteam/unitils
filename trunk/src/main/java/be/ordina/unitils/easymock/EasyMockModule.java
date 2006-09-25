package be.ordina.unitils.easymock;

import be.ordina.unitils.module.BaseUnitilsModule;

/**
 * todo javadoc
 */
public class EasyMockModule extends BaseUnitilsModule {


    public void beforeAll() throws Exception {
        super.beforeAll();
        System.out.println("EasyMockModule.beforeAll");
    }


    public void beforeTestClass(Object test) throws Exception {
        super.beforeTestClass(test);
        System.out.println("EasyMockModule.beforeTestClass " + test);
    }


    public void beforeTestMethod(Object test, String methodName) throws Exception {
        super.beforeTestMethod(test, methodName);
        System.out.println("EasyMockModule.beforeTestMethod " + test + " " + methodName);
    }

    public void afterTestMethod(Object test, String methodName) throws Exception {
        super.afterTestMethod(test, methodName);
        System.out.println("EasyMockModule.afterTestMethod " + test + " " + methodName);
    }


    public void afterTestClass(Object test) throws Exception {
        super.afterTestClass(test);
        System.out.println("EasyMockModule.afterTestClass " + test);
    }

    public void afterAll() throws Exception {
        super.afterAll();
        System.out.println("EasyMockModule.afterAll");
    }

}
