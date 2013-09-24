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

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class CglibProxyMethodInterceptorGetProxiedTypeTest {

    private CglibProxyMethodInterceptor<Map> cglibProxyMethodInterceptor;


    @Before
    public void initialize() throws Exception {
        cglibProxyMethodInterceptor = new CglibProxyMethodInterceptor<Map>("mockName", Map.class, null, null, null);
    }


    @Test
    public void getProxiedType() throws Throwable {
        Class<?> result = cglibProxyMethodInterceptor.getProxiedType();
        assertEquals(Map.class, result);
    }
}
