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

    /**
     * Invoked before any BeforeClass method is called on the test. At this moment only the test class is known,
     * the test instance and test method are not available.
     * <br/><br/>
     * Note: for JUnit 3 and also for spring 2.5.6 this method is called just before the {@link #beforeTest}
     * method because these do not support the before/after test class behavior.
     *
     * @param currentTestClass The current test class, not null
     */
    public void beforeTestClass(CurrentTestClass currentTestClass) throws Exception {
    }

    /**
     * Invoked after the Before method but before the actual test method is called on the test.
     *
     * @param currentTestInstance The current test instance, not null
     */
    public void beforeTest(CurrentTestInstance currentTestInstance) throws Exception {
    }

    /**
     * Invoked after all After methods are called on the test.
     *
     * @param currentTestInstance The current test class, not null
     */
    public void afterTest(CurrentTestInstance currentTestInstance) throws Exception {
    }

}
