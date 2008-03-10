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
package org.unitils.inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.util.PropertyAccess;

import java.util.Properties;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoStaticByTypeExceptionsTest extends UnitilsJUnit4 {

    private static final Log logger = LogFactory.getLog(InjectModuleInjectIntoStaticByTypeExceptionsTest.class);

    private TestInjectIntoStaticByType_NoPropertyOfType testInjectIntoStaticByType_noPropertyOfType = new TestInjectIntoStaticByType_NoPropertyOfType();
    private TestInjectIntoStaticByType_MoreThanOneFieldOfType testInjectIntoStaticByType_moreThanOneFieldOfType = new TestInjectIntoStaticByType_MoreThanOneFieldOfType();
    private TestInjectIntoStaticByType_MoreThanOneSetterOfType testInjectIntoStaticByType_moreThanOneSetterOfType = new TestInjectIntoStaticByType_MoreThanOneSetterOfType();
    private TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType testInjectIntoStaticByType_moreThanOneFieldOfSuperType = new TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType();
    private TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType testInjectIntoStaticByType_moreThanOneSetterOfSuperType = new TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType();

    private InjectModule injectModule = new InjectModule();


    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        injectModule.init(configuration);
    }


    @Test
    public void testInjectIntoStaticByType_noPropertyOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_noPropertyOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }


    @Test
    public void testInjectIntoStaticByType_moreThanOneFieldOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneFieldOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }


    @Test
    public void testInjectIntoStaticByType_moreThanOneSetterOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneSetterOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }


    @Test
    public void testInjectIntoStaticByType_moreThanOneFieldOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneFieldOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }


    @Test
    public void testInjectIntoStaticByType_moreThanOneSetterOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneSetterOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    @SuppressWarnings("unused")
    public class TestInjectIntoStaticByType_NoPropertyOfType {

        @InjectIntoStaticByType(target = InjectOn_NoPropertyOfType.class)
        private ToInject toInject;
    }

    @SuppressWarnings("unused")
    public class TestInjectIntoStaticByType_MoreThanOneFieldOfType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneFieldOfType.class)
        private ToInject toInject;
    }

    @SuppressWarnings("unused")
    public class TestInjectIntoStaticByType_MoreThanOneSetterOfType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneSetterOfType.class, propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;
    }

    @SuppressWarnings("unused")
    public class TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneFieldOfSuperType.class)
        private ToInject toInject;
    }

    @SuppressWarnings("unused")
    public class TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneSetterOfSuperType.class, propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;
    }

    public class ToInjectSuper {
    }

    /**
     * Object to inject
     */
    public class ToInject extends ToInjectSuper {
    }

    /**
     * Object to inject into
     */
    public class InjectOn {
    }

    public static class InjectOn_NoPropertyOfType {
    }

    @SuppressWarnings("unused")
    public static class InjectOn_MoreThanOneFieldOfType {

        private static ToInject toInject1;

        private static ToInject toInject2;

    }

    public static class InjectOn_MoreThanOneSetterOfType {

        public static void setToInject1(ToInject toInject1) {
        }

        public static void setToInject2(ToInject toInject2) {
        }

    }

    @SuppressWarnings("unused")
    public static class InjectOn_MoreThanOneFieldOfSuperType {

        private static ToInjectSuper toInject1;

        private static ToInjectSuper toInject2;

    }

    public static class InjectOn_MoreThanOneSetterOfSuperType {

        public static void setToInject1(ToInjectSuper toInject1) {
        }

        public static void setToInject2(ToInjectSuper toInject2) {
        }

    }
}
