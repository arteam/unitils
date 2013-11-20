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
package org.unitils.mock.core.proxy;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ProxyServiceGetProxiedMethodStackTraceTest {

    private ProxyService proxyService;


    @Before
    public void initialize() {
        proxyService = new ProxyService(null);
    }


    @Test
    public void stackTraceContainingProxy() {
        StackTraceElement[] stackTrace = new StackTraceElement[]{
                new StackTraceElement("class1", "method1", "file1", 1),
                new StackTraceElement("class2", "method2", "file2", 2),
                new StackTraceElement("class3$$", "method3", "file3", 3),
                new StackTraceElement("class4", "method4", "file4", 5),
                new StackTraceElement("class5", "method5", "file5", 6)
        };

        StackTraceElement[] result = proxyService.getProxiedMethodStackTrace(stackTrace);
        assertEquals(2, result.length);
        assertEquals("class4", result[0].getClassName());
        assertEquals("class5", result[1].getClassName());
    }

    @Test
    public void exceptionWhenNoProxyFound() {
        StackTraceElement[] stackTrace = new StackTraceElement[]{
                new StackTraceElement("class1", "method1", "file1", 1),
                new StackTraceElement("class2", "method2", "file2", 2)
        };
        try {
            proxyService.getProxiedMethodStackTrace(stackTrace);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No invocation of a cglib proxy method found in stack trace: [class1.method1(file1:1), class2.method2(file2:2)]", e.getMessage());
        }
    }
}
