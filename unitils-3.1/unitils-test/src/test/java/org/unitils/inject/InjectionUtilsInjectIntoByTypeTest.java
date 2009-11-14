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

import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.inject.util.InjectionUtils;
import static org.unitils.inject.util.PropertyAccess.FIELD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Test for the injection utils.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectionUtilsInjectIntoByTypeTest {

    private InjectTarget injectTarget = new InjectTarget();

    private Properties testProperties = new Properties();

    private Map<String, List<String>> testMap = new HashMap<String, List<String>>();


    @Test
    public void injectIntoByType() {
        InjectionUtils.injectIntoByType(testProperties, Properties.class, injectTarget, FIELD);
        assertSame(testProperties, injectTarget.properties);
    }


    @Test(expected = UnitilsException.class)
    public void genericsButMoreThanOneFieldWithSameRawType() {
        InjectionUtils.injectIntoByType(testMap, Map.class, injectTarget, FIELD);
    }


    public static class InjectTarget {

        public Properties properties;

        public Map<String, List<String>> map1;

        public Map<String, String> map2;

    }


}