/*
 * Copyright 2006 the original author or authors.
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
import org.apache.commons.configuration.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.CreateSpringApplicationContext;
import org.unitils.spring.annotation.SpringApplicationContext;

import java.util.List;

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

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        springModule = new SpringModule();
        springModule.init(configuration);
    }


    /**
     * Tests creating an application context using SpringApplicationContext
     */
    public void testGetApplicationContext() {
        SpringTest springTest = new SpringTest();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTest);

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
     * Tests creating an application context using class level annotation and 2 custom create methods.
     */
    public void testGetApplicationContext_mixing() {
        SpringTestMixing springTestMixing = new SpringTestMixing();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestMixing);

        assertNotNull(applicationContext);
        assertTrue(springTestMixing.createMethod1Called);
        assertTrue(springTestMixing.createMethod2Called);
    }


    /**
     * Tests getting an application context a second time, the same application context should be returned.
     */
    public void testGetApplicationContext_twice() {
        SpringTestMixing springTestMixing = new SpringTestMixing();
        ApplicationContext applicationContext1 = springModule.getApplicationContext(springTestMixing);
        ApplicationContext applicationContext2 = springModule.getApplicationContext(springTestMixing);

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
     * Test SpringTest class with class level locations.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {
    }

    /**
     * Test SpringTest class with a custom create method.
     */
    private class SpringTestCreateMethod {

        @CreateSpringApplicationContext
        protected ApplicationContext createMethod() {
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest class with a custom create method with application context argument.
     */
    private class SpringTestCreateMethodWithApplicationContext {

        @CreateSpringApplicationContext
        protected ApplicationContext createMethod(ApplicationContext applicationContext) {
            assertNull(applicationContext);
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest class with a custom create method.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestMixing {

        protected boolean createMethod1Called = false;
        protected boolean createMethod2Called = false;

        @CreateSpringApplicationContext
        protected ApplicationContext createMethod1(ApplicationContext applicationContext) {
            assertNotNull(applicationContext);
            createMethod1Called = true;
            return applicationContext;
        }

        @CreateSpringApplicationContext
        protected ApplicationContext createMethod2(ApplicationContext applicationContext) {
            assertNotNull(applicationContext);
            createMethod2Called = true;
            return applicationContext;
        }
    }

    /**
     * Test SpringTest class with a custom create method having a wrong signature.
     */
    private class SpringTestCreateMethodWrongSignature {

        @CreateSpringApplicationContext
        protected List createMethod(String a) {
            return null;
        }
    }

}
