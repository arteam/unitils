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
package org.unitils;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.unitils.AssertInvocationsBlockJUnit4ClassRunner.Invocation.*;
import static org.unitils.AssertInvocationsBlockJUnit4ClassRunner.addInvocations;

/**
 * Note: the assertion is done in the {@code AssertInvocationsUnitilsTestListener#afterClass}
 *
 * @author Tim Ducheyne
 */
@RunWith(AssertInvocationsBlockJUnit4ClassRunner.class)
public class UnitilsJUnit4IntegrationTest {

    @BeforeClass
    public static void beforeClass() {
        addInvocations(TEST_BEFORE_CLASS);
    }

    @Before
    public void before() {
        addInvocations(TEST_BEFORE);
    }

    @Test
    public void test() {
        addInvocations(TEST_METHOD);
    }

    @After
    public void after() {
        addInvocations(TEST_AFTER);
    }

    @AfterClass
    public static void afterClass() {
        addInvocations(TEST_AFTER_CLASS);
    }
}
