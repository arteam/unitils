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
package org.unitils.core.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.engine.UnitilsTestListener;
import org.unitils.mock.Mock;

import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 */
public class BeforeTestSetUpStatementEvaluateTest extends UnitilsJUnit4 {

    private BeforeTestSetUpStatement beforeTestSetUpStatement;

    private Mock<UnitilsTestListener> unitilsTestListenerMock;
    private Mock<Statement> statementMock;
    private MyTestClass testObject;
    private Method testMethod;


    @Before
    public void initialize() throws Exception {
        testObject = new MyTestClass();
        testMethod = MyTestClass.class.getDeclaredMethod("testMethod");
        beforeTestSetUpStatement = new BeforeTestSetUpStatement(testObject, testMethod, unitilsTestListenerMock.getMock(), statementMock.getMock());
    }


    @Test
    public void evaluate() throws Throwable {
        beforeTestSetUpStatement.evaluate();
        unitilsTestListenerMock.assertInvokedInSequence().beforeTestSetUp(testObject, testMethod);
        statementMock.assertInvokedInSequence().evaluate();
    }


    private static class MyTestClass {

        public void testMethod() {
        }
    }
}
