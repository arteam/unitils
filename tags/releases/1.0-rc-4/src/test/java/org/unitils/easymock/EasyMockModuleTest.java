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
package org.unitils.easymock;

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.easymock.annotation.Mock;

import java.util.List;
import java.util.Properties;

/**
 * Test class for the creating mocks using the {@link EasyMockModule}
 * <p/>
 * todo add more tests
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class EasyMockModuleTest extends UnitilsJUnit3 {

    /* Tested object */
    private EasyMockModule easyMockModule;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        easyMockModule = new EasyMockModule();
        easyMockModule.init(configuration);
    }


    /**
     * Test for creating a mock list.
     */
    public void testDataSet() throws Exception {
        MockTest mockTest = new MockTest();
        easyMockModule.createAndInjectMocksIntoTest(mockTest);
        assertNotNull(mockTest.testMock);
    }

    /**
     * Test class with a mock creation
     */
    public class MockTest {

        @Mock
        protected List<?> testMock;

    }
}