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
package org.unitils.inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.util.PropertyAccess;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoByTypeExceptionsTest extends UnitilsJUnit3 {

    private static final Log logger = LogFactory.getLog(InjectModuleInjectIntoByTypeExceptionsTest.class);

    private TestInjectIntoByType_TargetIsNull testInjectIntoByType_targetIsNull = new TestInjectIntoByType_TargetIsNull();
    private TestInjectIntoByType_TargetDoesntExist testInjectIntoByType_targetDoesntExist = new TestInjectIntoByType_TargetDoesntExist();
    private TestInjectIntoByType_NoTargetSpecified testInjectIntoByType_noTargetSpecified = new TestInjectIntoByType_NoTargetSpecified();
    private TestInjectIntoByType_NoPropertyOfType testInjectIntoByType_noPropertyOfType = new TestInjectIntoByType_NoPropertyOfType();
    private TestInjectIntoByType_MoreThanOneFieldOfType testInjectIntoByType_moreThanOneFieldOfType = new TestInjectIntoByType_MoreThanOneFieldOfType();
    private TestInjectIntoByType_MoreThanOneSetterOfType testInjectIntoByType_moreThanOneSetterOfType = new TestInjectIntoByType_MoreThanOneSetterOfType();
    private TestInjectIntoByType_MoreThanOneFieldOfSuperType testInjectIntoByType_moreThanOneFieldOfSuperType = new TestInjectIntoByType_MoreThanOneFieldOfSuperType();
    private TestInjectIntoByType_MoreThanOneSetterOfSuperType testInjectIntoByType_moreThanOneSetterOfSuperType = new TestInjectIntoByType_MoreThanOneSetterOfSuperType();

    private InjectModule injectModule = new InjectModule();

    protected void setUp() throws Exception {
        super.setUp();

        Configuration conf = Unitils.getInstance().getConfiguration();
        injectModule.init(conf);
    }

     public void testInject_targetIsNull() {
        try {
            injectModule.injectObjects(testInjectIntoByType_targetIsNull);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_targetDoesntExist() {
        try {
            injectModule.injectObjects(testInjectIntoByType_targetDoesntExist);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_noTargetSpecified() {
        try {
            injectModule.injectObjects(testInjectIntoByType_noTargetSpecified);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_noPropertyOfType() {
        try {
            injectModule.injectObjects(testInjectIntoByType_noPropertyOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_moreThanOneFieldOfType() {
        try {
            injectModule.injectObjects(testInjectIntoByType_moreThanOneFieldOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_moreThanOneSetterOfType() {
        try {
            injectModule.injectObjects(testInjectIntoByType_moreThanOneSetterOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_moreThanOneFieldOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoByType_moreThanOneFieldOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoByType_moreThanOneSetterOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoByType_moreThanOneSetterOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

     public class TestInjectIntoByType_TargetIsNull {

        @InjectIntoByType(target = "injectOn")
        private ToInject toInject;

        private InjectOn injectOn;

    }

    public class TestInjectIntoByType_TargetDoesntExist {

        @InjectIntoByType(target = "nonExisting")
        private ToInject toInject;
    }

    public class TestInjectIntoByType_NoTargetSpecified {

        @InjectIntoByType
        private ToInject toInject;
    }

    public class TestInjectIntoByType_NoPropertyOfType {

        @InjectIntoByType(target = "injectOn")
        private ToInject toInject;

        private InjectOn_NoPropertyOfType injectOn = new InjectOn_NoPropertyOfType();
    }

    public class TestInjectIntoByType_MoreThanOneFieldOfType {

        @InjectIntoByType(target = "injectOn")
        private ToInject toInject;

        private InjectOn_MoreThanOneFieldOfType injectOn = new InjectOn_MoreThanOneFieldOfType();
    }

    public class TestInjectIntoByType_MoreThanOneSetterOfType {

        @InjectIntoByType(target = "injectOn", propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;

        private InjectOn_MoreThanOneSetterOfType injectOn = new InjectOn_MoreThanOneSetterOfType();
    }

    public class TestInjectIntoByType_MoreThanOneFieldOfSuperType {

        @InjectIntoByType(target = "injectOn")
        private ToInject toInject;

        private InjectOn_MoreThanOneFieldOfSuperType injectOn = new InjectOn_MoreThanOneFieldOfSuperType();
    }

    public class TestInjectIntoByType_MoreThanOneSetterOfSuperType {

        @InjectIntoByType(target = "injectOn", propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;

        private InjectOn_MoreThanOneSetterOfSuperType injectOn = new InjectOn_MoreThanOneSetterOfSuperType();
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

    public class InjectOn_NoPropertyOfType {
    }

    public class InjectOn_MoreThanOneFieldOfType {

        private ToInject toInject1;

        private ToInject toInject2;

    }

    public class InjectOn_MoreThanOneSetterOfType {

        public void setToInject1(ToInject toInject1) {
        }

        public void setToInject2(ToInject toInject2) {
        }

    }

    public class InjectOn_MoreThanOneFieldOfSuperType {

        private ToInjectSuper toInject1;

        private ToInjectSuper toInject2;

    }

    public class InjectOn_MoreThanOneSetterOfSuperType {

        public void setToInject1(ToInjectSuper toInject1) {
        }

        public void setToInject2(ToInjectSuper toInject2) {
        }

    }
}
