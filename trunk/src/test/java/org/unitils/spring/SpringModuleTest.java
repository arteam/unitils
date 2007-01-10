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
import org.unitils.core.ConfigurationLoader;
import org.unitils.spring.annotation.*;

import java.util.List;

/**
 * Test for the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleTest extends TestCase {

    /* Tested object */
    private SpringModule springModule;

    private SpringTest springTest;

    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        springModule = new SpringModule();
        springModule.init(configuration);

        springTest = new SpringTest();
    }


    /**
     * todo javadoc
     */
    public void testInjectSpringBeans() {
        springModule.getSpringBeans(springTest);

        assertTrue(springTest.testBean1 instanceof String);
    }


    /**
     * todo javadoc
     */
    public void testInjectSpringBeansByType() {
        springModule.getSpringBeansByType(springTest);

        assertNotNull(springTest.testBeanByType);
    }


    /**
     * todo javadoc
     */
    public void testInjectSpringBeansByName() {
        springModule.getSpringBeansByName(springTest);

        assertNotNull(springTest.testBeanByName);
    }


    /**
     * Test SpringTest class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {

        @SpringBean("aBeanName")
        private String testBean1 = null;

        @SpringBeanByType
        private List testBeanByType = null;

        @SpringBeanByName
        private String testBeanByName = null;


        @CreateSpringApplicationContext
        protected ApplicationContext createMethod() {
            //todo
            return null;
        }

    }

}
