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
package org.unitils.mock.core;

import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.DummyProxyInvocationHandler;
import org.unitils.mock.mockbehavior.impl.DummyValueReturningMockBehavior;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * @author Tim Ducheyne
 */
public class DummyService {

    protected ProxyService proxyService;


    public DummyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }


    /**
     * Creates a dummy of the given type. A dummy object is a proxy that will return default values for every method. This can be
     * used to quickly create test objects without having to worry about correctly filling in every field.
     *
     * @param name The name for the dummy, use null for the default typeDummy name
     * @param type The type for the proxy, not null
     * @return The proxy, not null
     */
    public <T> T createDummy(String name, Class<T> type) {
        String dummyName = getDummyName(name, type);
        DummyValueReturningMockBehavior mockBehaviour = new DummyValueReturningMockBehavior(this);
        return proxyService.createUninitializedProxy(dummyName, new DummyProxyInvocationHandler(type, mockBehaviour), type);
    }


    protected <T> String getDummyName(String name, Class<T> dummyType) {
        if (isBlank(name)) {
            return uncapitalize(dummyType.getSimpleName()) + "Dummy";
        }
        return name;
    }
}
