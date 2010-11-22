/*
 * Copyright Unitils.org
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
package org.unitils.core;

/**
 * Empty test execution listener implementation. Override methods to add behavior.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class TestListener {


    public void beforeTestClass(CurrentTestClass currentTestClass) throws Exception {
    }

    public void prepareTestInstance(CurrentTestInstance currentTestInstance) throws Exception {
    }

    public void beforeTestMethod(CurrentTestInstance currentTestInstance) throws Exception {
    }

    public void afterTestMethod(CurrentTestInstance currentTestInstance) throws Exception {
    }

}
