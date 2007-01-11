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
import org.unitils.spring.annotation.SpringBean;

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
    public void testCreateApplicationContext() {
        SpringTest springTest = new SpringTest();
        springModule.assignSpringBeans(springTest);

        assertTrue(springTest.testBean instanceof String);
    }


    /**
     * Tests creating an application context using a custom create method.
     */
    public void testCreateApplicationContext_customCreate() {
        SpringTestCreateMethod springTestCreateMethod = new SpringTestCreateMethod();
        springModule.assignSpringBeans(springTestCreateMethod);

        assertTrue(springTestCreateMethod.testBean instanceof String);
    }


    /**
     * Tests creating an application context using a custom create method with an application context argument.
     */
    public void testCreateApplicationContext_customCreateWithApplicationContext() {
        SpringTestCreateMethodWithApplicationContext springTestCreateMethod = new SpringTestCreateMethodWithApplicationContext();
        springModule.assignSpringBeans(springTestCreateMethod);

        assertTrue(springTestCreateMethod.testBean instanceof String);
    }


    /**
     * Tests creating an application context using class level annotation and 2 custom create methods.
     */
    public void testCreateApplicationContext_mixing() {
        SpringTestMixing springTestMixing = new SpringTestMixing();
        springModule.assignSpringBeans(springTestMixing);

        assertTrue(springTestMixing.testBean instanceof String);
        assertTrue(springTestMixing.createMethod1Called);
        assertTrue(springTestMixing.createMethod2Called);
    }


    /**
     * Tests creating an application context using a custom create method with a wrong signature.
     */
    public void testCreateApplicationContext_customCreateWrongSignature() {
        SpringTestCreateMethodWrongSignature springTestCreateMethodWrongSignature = new SpringTestCreateMethodWrongSignature();
        try {
            springModule.assignSpringBeans(springTestCreateMethodWrongSignature);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
        assertNull(springTestCreateMethodWrongSignature.testBean);
    }


    /**
     * Test SpringTest class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {

        @SpringBean("aBeanName")
        private String testBean = null;
    }

    /**
     * Test SpringTest class with a custom create method.
     */
    private class SpringTestCreateMethod {

        @SpringBean("aBeanName")
        private String testBean = null;

        @CreateSpringApplicationContext
        protected ApplicationContext createMethod() {
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest class with a custom create method with application context argument.
     */
    private class SpringTestCreateMethodWithApplicationContext {

        @SpringBean("aBeanName")
        private String testBean = null;

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

        @SpringBean("aBeanName")
        private String testBean = null;

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

        @SpringBean("aBeanName")
        private String testBean = null;

        @CreateSpringApplicationContext
        protected List createMethod(String a) {
            return null;
        }
    }

}
