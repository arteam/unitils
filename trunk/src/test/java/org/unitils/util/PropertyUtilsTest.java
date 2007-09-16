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
package org.unitils.util;

import junit.framework.TestCase;
import org.unitils.core.UnitilsException;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.util.PropertyUtils.*;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Properties;

/**
 * Test for {@link PropertyUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PropertyUtilsTest extends TestCase {


    /* A test properties instance */
    private Properties testProperties;


    /**
     * Sets up the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        testProperties = new Properties();
        testProperties.setProperty("stringProperty", "test");
        testProperties.setProperty("stringListProperty", "test1, test2, test3 , ,");
        testProperties.setProperty("booleanProperty", "true");
        testProperties.setProperty("longProperty", "5");
        testProperties.setProperty("instanceProperty", "java.lang.StringBuffer");
    }


    /**
     * Test for getting a string property
     */
    public void testGetString() {
        String result = getString("stringProperty", testProperties);
        assertEquals("test", result);
    }


    /**
     * Test for getting an unknown string property
     */
    public void testGetString_notFound() {
        try {
            getString("xxxx", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a string property passing a default
     */
    public void testGetString_default() {
        String result = getString("stringProperty", "default", testProperties);
        assertEquals("test", result);
    }


    /**
     * Test for getting an unknown string property passing a default
     */
    public void testGetString_defaultNotFound() {
        String result = getString("xxxx", "default", testProperties);
        assertEquals("default", result);
    }


    /**
     * Test for getting a string list property
     */
    public void testGetStringList() {
        List<String> result = getStringList("stringListProperty", testProperties);
        assertLenEquals(asList("test1", "test2", "test3", ""), result);
    }


    /**
     * Test for getting an unknown string list property
     */
    public void testGetStringList_notFound() {
        List<String> result = getStringList("xxxx", testProperties);
        assertTrue(result.isEmpty());
    }


    /**
     * Test for getting an unknown string list property
     */
    public void testGetStringList_requiredNotFound() {
        try {
            getStringList("xxxx", testProperties, true);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a boolean property
     */
    public void testGetBoolean() {
        boolean result = getBoolean("booleanProperty", testProperties);
        assertTrue(result);
    }


    /**
     * Test for getting an unknown boolean property
     */
    public void testGetBoolean_notFound() {
        try {
            getBoolean("xxxx", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a boolean property passing a default
     */
    public void testGetBoolean_default() {
        boolean result = getBoolean("booleanProperty", false, testProperties);
        assertTrue(result);
    }


    /**
     * Test for getting an unknown boolean property passing a default
     */
    public void testGetBoolean_defaultNotFound() {
        boolean result = getBoolean("xxxx", false, testProperties);
        assertFalse(result);
    }


    /**
     * Test for getting a long property
     */
    public void testGetLong() {
        long result = getLong("longProperty", testProperties);
        assertEquals(5, result);
    }


    /**
     * Test for getting a long property that is not a number
     */
    public void testGetLong_notNumber() {
        try {
            getLong("stringProperty", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting an unknown long property
     */
    public void testGetLong_notFound() {
        try {
            getLong("xxxx", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a long property passing a default
     */
    public void testGetLong_default() {
        long result = getLong("longProperty", 10, testProperties);
        assertEquals(5, result);
    }


    /**
     * Test for getting a long property that is not a number passing a default
     */
    public void testGetLong_defaultNotNumber() {
        try {
            getLong("stringProperty", 10, testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting an unknown long property passing a default
     */
    public void testGetLong_defaultNotFound() {
        long result = getLong("xxxx", 10, testProperties);
        assertEquals(10, result);
    }


    /**
     * Test for getting an object instance.
     */
    public void testGetInstance() {
        Object result = getInstance("instanceProperty", testProperties);
        assertTrue(result instanceof StringBuffer);
    }


    /**
     * Test for getting an unknown object instance property
     */
    public void testGetInstance_notFound() {
        try {
            getInstance("xxxx", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a object instance property that does not contain a class name
     */
    public void testGetInstance_couldNotCreate() {
        try {
            getInstance("stringProperty", testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting a object instance property passing a default
     */
    public void testGetInstance_default() {
        Object result = getInstance("instanceProperty", new ArrayList<Object>(), testProperties);
        assertTrue(result instanceof StringBuffer);
    }


    /**
     * Test for getting an unknown object instance property passing a default
     */
    public void testGetInstance_defaultNotFound() {
        Object result = getInstance("xxxx", new ArrayList<Object>(), testProperties);
        assertTrue(result instanceof ArrayList);
    }


    /**
     * Test for getting a object instance property that does not contain a class name passing a default
     */
    public void testGetInstance_defaultCouldNotCreate() {
        try {
            getInstance("stringProperty", new ArrayList<Object>(), testProperties);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

}
