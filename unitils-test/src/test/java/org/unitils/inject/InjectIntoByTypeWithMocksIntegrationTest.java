/*
 * Copyright 2013,  Unitils.org
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

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoByTypeWithMocksIntegrationTest extends UnitilsJUnit4 {

    @TestedObject
    public InjectionTarget injectionTarget;

    @InjectIntoByType
    @InjectIntoStaticByType(target = InjectionTarget.class)
    public Mock<Properties> mock;
    @InjectIntoByType
    public Mock<Map<String, List<String>>> mockWithGenericType;


    @Test
    public void injectIntoByTypeWithMock() {
        // mock type
        assertSame(mock.getMock(), injectionTarget.field);
        // generic mock type
        assertSame(mockWithGenericType.getMock(), injectionTarget.genericField);
        // static injection
        assertSame(mock.getMock(), InjectionTarget.staticField);
    }


    private static class InjectionTarget {

        private static Properties staticField;

        private Properties field;

        private Map<String, List<String>> genericField;

        private Map<String, String> genericFieldWithSameRawType;

    }
}