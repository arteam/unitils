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
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import org.unitils.spring.annotation.SpringApplicationContext;

import static java.util.Arrays.asList;
import java.util.List;

/**
 * Test for ApplicationContext creation in a test class hierarchy for the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleApplicationContextInheritanceTest extends TestCase {

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
     * Tests creating the application context. First the context of the super class should be created, followed by
     * the context of the subclass. The context of the superclass should have been set as parent of the subclass context.
     */
    public void testCreateApplicationContext() {
        SpringTest1 springTest1 = new SpringTest1();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTest1);

        assertNotNull(applicationContext);
        assertFalse(springTest1.createMethod1Called);
        assertTrue(springTest1.createMethod2Called);
    }


    /**
     * Test creating an application context for 2 subclasses of the same superclass. The context of the
     * superclass (parent) should have been reused.
     */
    public void testCreateApplicationContext_twice() {
        ApplicationContext applicationContext1 = springModule.getApplicationContext(new SpringTestNoCreation1());
        ApplicationContext applicationContext2 = springModule.getApplicationContext(new SpringTestNoCreation2());

        assertNotNull(applicationContext1);
        assertSame(applicationContext1, applicationContext2);
    }


    /**
     * Tests creating the application context. No context creation is done in the sub-class, the context of the super
     * class should be used.
     */
    public void testCreateApplicationContext_onlyInSuperClass() {
        SpringTestNoCreation1 springTestNoCreation = new SpringTestNoCreation1();
        ApplicationContext applicationContext = springModule.getApplicationContext(springTestNoCreation);

        assertNotNull(applicationContext);
        assertTrue(springTestNoCreation.createMethod1Called);
    }


    /**
     * Test SpringTest super-class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestSuper {

        protected boolean createMethod1Called = false;

        @SpringApplicationContext
        protected ApplicationContext createMethod1(List<String> locations) {
            createMethod1Called = true;
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest sub-class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest1 extends SpringTestSuper {

        protected boolean createMethod2Called = false;

        @SpringApplicationContext
        protected ApplicationContext createMethod2(List<String> locations) {
            createMethod2Called = true;
            assertLenEquals(asList("classpath:org/unitils/spring/services-config.xml", "classpath:org/unitils/spring/services-config.xml"), locations);
            createMethod2Called = true;
            return new ClassPathXmlApplicationContext("classpath:org/unitils/spring/services-config.xml");
        }
    }

    /**
     * Test SpringTest sub-class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest2 extends SpringTestSuper {
    }


    /**
     * Test SpringTest sub-class without any context declaration.
     */
    private class SpringTestNoCreation1 extends SpringTestSuper {
    }

    /**
     * Test SpringTest sub-class without any context declaration.
     */
    private class SpringTestNoCreation2 extends SpringTestSuper {
    }


}
