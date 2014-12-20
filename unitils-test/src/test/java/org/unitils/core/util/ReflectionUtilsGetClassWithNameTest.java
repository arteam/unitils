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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsGetClassWithNameTest {


    @Test
    public void getClassWithName() {
        Class<Properties> result = ReflectionUtils.getClassWithName("java.util.Properties");
        assertNotNull(result);
    }

    @Test
    public void exceptionWhenClassNotFound() {
        try {
            ReflectionUtils.getClassWithName("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Could not load class with name xxx\n" +
                    "Reason: ClassNotFoundException: xxx", e.getMessage());
        }
    }
}