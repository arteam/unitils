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
package org.unitils.mock.core;

import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.core.BehaviorDefiningInvocation;

import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ObservedInvocation {

    private String mockName;

    private ProxyInvocation proxyInvocation;

    private List<String> argumentsAsStrings;

    private String resultAsString;

    private BehaviorDefiningInvocation behaviorDefiningInvocation;

    private MockBehavior mockBehavior;


    public ObservedInvocation(String mockName, ProxyInvocation proxyInvocation, List<String> argumentsAsStrings, String resultAsString, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        this.mockName = mockName;
        this.proxyInvocation = proxyInvocation;
        this.argumentsAsStrings = argumentsAsStrings;
        this.resultAsString = resultAsString;
        this.behaviorDefiningInvocation = behaviorDefiningInvocation;
        this.mockBehavior = mockBehavior;
    }


    public String getMockName() {
        return mockName;
    }


    public ProxyInvocation getProxyInvocation() {
        return proxyInvocation;
    }


    public List<String> getArgumentsAsStrings() {
        return argumentsAsStrings;
    }


    public String getResultAsString() {
        return resultAsString;
    }


    public BehaviorDefiningInvocation getBehaviorDefiningInvocation() {
        return behaviorDefiningInvocation;
    }


    public MockBehavior getMockBehavior() {
        return mockBehavior;
    }


    public boolean hasMockBehavior() {
        return mockBehavior != null;
    }
}