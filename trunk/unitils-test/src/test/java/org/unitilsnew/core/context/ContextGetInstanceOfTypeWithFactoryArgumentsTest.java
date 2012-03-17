/*
 * Copyright 2010,  Unitils.org
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

package org.unitilsnew.core.context;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ContextGetInstanceOfTypeWithFactoryArgumentsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }

    @Test
    public void factoryAsImplementationType() {
        configurationMock.returns(FactoryClass.class.getName()).getOptionalString(Map.class.getName());

        Map result = context.getInstanceOfType(Map.class);
        assertTrue(result instanceof Properties);
    }

    @Test
    public void factoryAsArgumentImplementationType() {
        configurationMock.returns(FactoryClass.class.getName()).getOptionalString(Map.class.getName());

        TestClassA result = context.getInstanceOfType(TestClassA.class);
        assertTrue(result.map instanceof Properties);
    }

    @Test
    public void sameInstanceIsReturnedForSecondCall() {
        configurationMock.returns(FactoryClass.class.getName()).getOptionalString(Map.class.getName());

        Map result1 = context.getInstanceOfType(Map.class);
        Map result2 = context.getInstanceOfType(Map.class);
        assertSame(result1, result2);
    }

    @Test(expected = UnitilsException.class)
    public void factoryCreatesInstanceOfWrongType() {
        configurationMock.returns(FactoryClass.class.getName()).getOptionalString(List.class.getName());

        context.getInstanceOfType(List.class);
    }


    protected static class FactoryClass implements Factory<Map> {

        public Map create() {
            return new Properties();
        }
    }

    protected static class TestClassA {

        protected Map map;

        public TestClassA(Map map) {
            this.map = map;
        }
    }

    protected static class TestClassB {

        protected Map map;

        public TestClassB(@Property("map") Map map) {
            this.map = map;
        }
    }
}
