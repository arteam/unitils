/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.spring;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.SpringApplicationContext;

import java.util.List;
import java.util.Properties;

/**
 * Test for ApplicationContext creation in the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleApplicationContextTest extends TestCase {

    /* Tested object */
    private SpringModule springModule;


    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        springModule = new SpringModule();
        springModule.init(configuration);
    }


    /**
     * Tests creating an application context using SpringApplicationContext on class level
     */
    public void testGetApplicationContext() {
        SpringTest springTest = new SpringTest();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTest);

        assertNotNull(applicationContext);
    }


    /**
     * Tests creating an application context using SpringApplicationContext on field level
     */
    public void testGetApplicationContext_field() {
        SpringTestField springTestField = new SpringTestField();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestField);

        assertNotNull(applicationContext);
    }


    /**
     * Tests creating an application context using SpringApplicationContext on field level
     */
    public void testGetApplicationContext_setter() {
        SpringTestSetter springTestSetter = new SpringTestSetter();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestSetter);

        assertNotNull(applicationContext);
    }


    /**
     * Tests creating an application context using a custom create method.
     */
    public void testGetApplicationContext_customCreate() {
        SpringTestCreateMethod springTestCreateMethod = new SpringTestCreateMethod();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestCreateMethod);

        assertNotNull(applicationContext);
    }


    /**
     * Tests creating an application context using a custom create method with an application context argument.
     */
    public void testGetApplicationContext_customCreateWithApplicationContext() {
        SpringTestCreateMethodWithApplicationContext springTestCreateMethod = new SpringTestCreateMethodWithApplicationContext();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestCreateMethod);

        assertNotNull(applicationContext);
    }


    /**
     * Tests for more than 1 annotation with values. An exception should have been raised.
     */
    public void testGetApplicationContext_multipleAnnotationsWithValues() {
        SpringTestMultipleAnnotationsWithValues springTestMultipleAnnotationsWithValues = new SpringTestMultipleAnnotationsWithValues();
        try {
            springModule.getApplicationContext(springTestMultipleAnnotationsWithValues);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests for more than 1 custom create methods. An exception should have been raised.
     */
    public void testGetApplicationContext_twoCustomCreateMethods() {
        SpringTestTwoCreateMethods springTestTwoCreateMethods = new SpringTestTwoCreateMethods();
        try {
            springModule.getApplicationContext(springTestTwoCreateMethods);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests getting an application context a second time, the same application context should be returned.
     */
    public void testGetApplicationContext_twice() {
        SpringTest springTestMixing = new SpringTest();
        ApplicationContext applicationContext1 = springModule.getApplicationContext(springTestMixing);
        ApplicationContext applicationContext2 = springModule.getApplicationContext(springTestMixing);

        assertNotNull(applicationContext1);
        assertSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests invalidating a cached application context.
     */
    public void testInvalidateApplicationContext() {
        SpringTest springTestMixing = new SpringTest();
        ApplicationContext applicationContext1 = springModule.getApplicationContext(springTestMixing);
        springModule.invalidateApplicationContext();
        ApplicationContext applicationContext2 = springModule.getApplicationContext(springTestMixing);

        assertNotNull(applicationContext1);
        assertNotNull(applicationContext2);
        assertNotSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests invalidating a cached application context using the class name.
     */
    public void testInvalidateApplicationContext_classSpecified() {
        SpringTest springTestMixing = new SpringTest();
        ApplicationContext applicationContext1 = springModule.getApplicationContext(springTestMixing);
        springModule.invalidateApplicationContext(SpringTest.class);
        ApplicationContext applicationContext2 = springModule.getApplicationContext(springTestMixing);

        assertNotNull(applicationContext1);
        assertNotNull(applicationContext2);
        assertNotSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests invalidating a cached application context using a wrong class name.
     */
    public void testInvalidateApplicationContext_otherClassSpecified() {
        SpringTest springTestMixing = new SpringTest();
        ApplicationContext applicationContext1 = springModule.getApplicationContext(springTestMixing);
        springModule.invalidateApplicationContext(String.class, List.class);
        ApplicationContext applicationContext2 = springModule.getApplicationContext(springTestMixing);

        assertNotNull(applicationContext1);
        assertSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests creating an application context using a custom create method with a wrong signature.
     */
    public void testGetApplicationContext_customCreateWrongSignature() {
        SpringTestCreateMethodWrongSignature springTestCreateMethodWrongSignature = new SpringTestCreateMethodWrongSignature();
        try {
            springModule.getApplicationContext(springTestCreateMethodWrongSignature);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests creating an application context for an unknown location.
     */
    public void testGetApplicationContext_wrongLocation() {
        SpringTestWrongLocation springTestWrongLocation = new SpringTestWrongLocation();
        try {
            springModule.getApplicationContext(springTestWrongLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test SpringTest class with class level locations.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {
    }

    /**
     * Test SpringTest class with field level locations.
     */
    private class SpringTestField {

        @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"})
        protected ApplicationContext field = null;

    }

    /**
     * Test SpringTest class with setter level locations.
     */
    private class SpringTestSetter {

        @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"})
        public void setField(ApplicationContext field) {
        }
    }

    /**
     * Test SpringTest class with a custom create method.
     */
    private class SpringTestCreateMethod {

        @SpringApplicationContext
        protected ApplicationContext createMethod() {
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest class with a custom create method with application context argument.
     */
    private class SpringTestCreateMethodWithApplicationContext {

        @SpringApplicationContext
        protected ApplicationContext createMethod(List<String> locations) {
            assertTrue(locations.isEmpty());
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest class mixing multiple annotations.
     */
    @SpringApplicationContext({"1"})
    private class SpringTestMultipleAnnotationsWithValues {

        @SpringApplicationContext({"2"})
        protected ApplicationContext field1;

        @SpringApplicationContext({"3"})
        protected ApplicationContext field2;
    }

    /**
     * Test SpringTest class with 2 custom create methods.
     */
    private class SpringTestTwoCreateMethods {

        @SpringApplicationContext
        protected ApplicationContext createMethod1(List<String> locations) {
            return null;
        }

        @SpringApplicationContext
        protected ApplicationContext createMethod2(List<String> locations) {
            return null;
        }
    }

    /**
     * Test SpringTest class with a custom create method having a wrong signature.
     */
    private class SpringTestCreateMethodWrongSignature {

        @SpringApplicationContext
        protected List<?> createMethod(String a) {
            return null;
        }
    }

    /**
     * Class level configuration a wrong location specified.
     */
    @SpringApplicationContext("xxxxxxx")
    public class SpringTestWrongLocation {
    }

}
