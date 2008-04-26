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
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Test for the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleSpringBeansTest extends TestCase {

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
     * Tests assigning a spring bean using its name.
     */
    public void testAssignSpringBeans() {
        SpringTest springTest = new SpringTest();
        springModule.assignSpringBeans(springTest);

        assertNotNull(springTest.testBean);
        assertNotNull(springTest.testBeanSetter);
    }


    /**
     * Tests assigning a spring bean by declared type of field and setter.
     */
    public void testAssignSpringBeansByType() {
        SpringTestByType springTestByType = new SpringTestByType();
        springModule.assignSpringBeansByType(springTestByType);

        assertNotNull(springTestByType.testBean);
        assertNotNull(springTestByType.testBeanSetter);
    }


    /**
     * Tests assigning a spring bean by name of field.
     */
    public void testAssignSpringBeansByName() {
        SpringTestByName springTestByName = new SpringTestByName();
        springModule.assignSpringBeansByName(springTestByName);

        assertNotNull(springTestByName.testBeanByName);
    }


    /**
     * Tests assigning a spring bean by name of setter.
     */
    public void testAssignSpringBeansByName_setter() {
        SpringTestByNameSetter springTestByNameSetter = new SpringTestByNameSetter();
        springModule.assignSpringBeansByName(springTestByNameSetter);

        assertNotNull(springTestByNameSetter.testBeanByName);
    }


    /**
     * Tests assigning a spring bean using an unexisting name.
     */
    public void testAssignSpringBeans_notFound() {
        SpringTestNotFound springTestNotFound = new SpringTestNotFound();
        try {
            springModule.assignSpringBeans(springTestNotFound);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
        assertNull(springTestNotFound.testBean);
    }


    /**
     * Tests assigning a spring bean using an unexisting type.
     */
    public void testAssignSpringBeansByType_notFound() {
        SpringTestByTypeNotFound springTestByTypeNotFound = new SpringTestByTypeNotFound();
        try {
            springModule.assignSpringBeansByType(springTestByTypeNotFound);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
        assertNull(springTestByTypeNotFound.testBean);
    }


    /**
     * Tests assigning a spring bean using an ambiguous type (TreeSet, HashSet).
     */
    public void testAssignSpringBeansByType_ambiguous() {
        SpringTestByTypeAmbiguous springTestByTypeAmbiguous = new SpringTestByTypeAmbiguous();
        try {
            springModule.assignSpringBeansByType(springTestByTypeAmbiguous);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
        assertNull(springTestByTypeAmbiguous.testBean);
    }


    /**
     * Tests assigning a spring bean using an unexisting field name.
     */
    public void testAssignSpringBeansByName_notFound() {
        SpringTestByNameNotFound springTestByNameNotFound = new SpringTestByNameNotFound();
        try {
            springModule.assignSpringBeansByName(springTestByNameNotFound);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
        assertNull(springTestByNameNotFound.xxxx);
    }


    /**
     * Test SpringTest class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {

        @SpringBean("aBeanName")
        private String testBean = null;

        private String testBeanSetter = null;

        @SpringBean("aBeanName")
        public void setTestBeanSetter(String testBeanSetter) {
            this.testBeanSetter = testBeanSetter;
        }
    }

    /**
     * Test SpringTest class by type.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByType {

        @SpringBeanByType
        private List testBean = null;

        private List testBeanSetter = null;

        @SpringBeanByType
        public void setTestBeanSetter(List testBeanSetter) {
            this.testBeanSetter = testBeanSetter;
        }
    }

    /**
     * Test SpringTest class by type but type is ambiguous (TreeSet and HashSet).
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByTypeAmbiguous {

        @SpringBeanByType
        private Set testBean = null;
    }

    /**
     * Test SpringTest class by name.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByName {

        @SpringBeanByName
        private String testBeanByName = null;
    }

    /**
     * Test SpringTest class by name.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByNameSetter {

        private String testBeanByName = null;

        @SpringBeanByName
        public void setTestBeanByName(String testBeanByName) {
            this.testBeanByName = testBeanByName;
        }
    }

    /**
     * Test SpringTest class with unexisting bean.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestNotFound {

        @SpringBean("xxxxx")
        private String testBean = null;
    }

    /**
     * Test SpringTest class by type (Map) that is not in context.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByTypeNotFound {

        @SpringBeanByType
        private Map testBean = null;
    }

    /**
     * Test SpringTest class by name that is not in context.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTestByNameNotFound {

        @SpringBeanByName
        private String xxxx = null;
    }

}