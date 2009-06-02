/*
 * Copyright 2008,  Unitils.org
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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.Scenario;

import java.util.Map;
import java.util.Properties;

/**
 * Test for the auto injection behavior when using Mock instances
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoByTypeWithMocksTest {

    /* Tested object */
    private InjectModule injectModule;


    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        injectModule = new InjectModule();
        injectModule.init(configuration);
    }


    @Test
    public void injectIntoByTypeWithMock() {
        InjectIntoByTypeWithMock injectIntoByTypeWithMock = new InjectIntoByTypeWithMock();
        injectModule.injectObjects(injectIntoByTypeWithMock);

        assertSame(injectIntoByTypeWithMock.mockedProperties.getMock(), injectIntoByTypeWithMock.injectTarget.properties);
    }


    @Test
    public void injectIntoStaticByTypeWithMock() {
        InjectIntoStaticByTypeWithMock injectIntoStaticByTypeWithMock = new InjectIntoStaticByTypeWithMock();
        injectModule.injectObjects(injectIntoStaticByTypeWithMock);

        assertSame(injectIntoStaticByTypeWithMock.mockedProperties.getMock(), InjectStaticTarget.properties);
    }


    @Test
    public void noFieldOfMockedTypeFound() {
        try {
            NoFieldOfMockedTypeFound noFieldOfMockedTypeFound = new NoFieldOfMockedTypeFound();
            injectModule.injectObjects(noFieldOfMockedTypeFound);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("No static field with (super)type Map found in InjectStaticTarget"));
        }
    }


    public static class InjectIntoByTypeWithMock {

        @TestedObject
        public InjectTarget injectTarget = new InjectTarget();

        @InjectIntoByType
        public Mock<Properties> mockedProperties = new MockObject<Properties>("test", Properties.class, false, new Scenario());
    }


    public static class InjectIntoStaticByTypeWithMock {

        @InjectIntoStaticByType(target = InjectStaticTarget.class)
        public Mock<Properties> mockedProperties = new MockObject<Properties>("test", Properties.class, false, new Scenario());
    }


    public static class NoFieldOfMockedTypeFound {

        @InjectIntoStaticByType(target = InjectStaticTarget.class)
        public Mock<Map> mockedProperties = new MockObject<Map>("test", Map.class, false, new Scenario());
    }


    public static class InjectTarget {

        public Properties properties;
    }


    public static class InjectStaticTarget {

        public static Properties properties;
    }


}