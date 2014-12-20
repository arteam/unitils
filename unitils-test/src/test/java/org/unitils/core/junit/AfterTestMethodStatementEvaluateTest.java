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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class AfterTestMethodStatementEvaluateTest extends UnitilsJUnit4 {

    private AfterTestMethodStatement afterTestMethodStatement;

    private Mock<UnitilsTestListener> unitilsTestListenerMock;
    private Mock<Statement> statementMock;


    @Before
    public void initialize() {
        afterTestMethodStatement = new AfterTestMethodStatement(unitilsTestListenerMock.getMock(), statementMock.getMock());
    }


    @Test
    public void evaluate() throws Throwable {
        afterTestMethodStatement.evaluate();
        statementMock.assertInvokedInSequence().evaluate();
        unitilsTestListenerMock.assertInvokedInSequence().afterTestMethod(null);
    }

    @Test
    public void exceptionDuringEvaluate() throws Throwable {
        NullPointerException exception = new NullPointerException("expected");
        statementMock.raises(exception).evaluate();
        try {
            afterTestMethodStatement.evaluate();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertSame(exception, e);
            unitilsTestListenerMock.assertInvokedInSequence().afterTestMethod(exception);
        }
    }

    @Test
    public void exceptionDuringAfterTestMethod() throws Throwable {
        NullPointerException exception = new NullPointerException("expected");
        unitilsTestListenerMock.raises(exception).afterTestMethod(null);
        try {
            afterTestMethodStatement.evaluate();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertSame(exception, e);
        }
    }

    @Test
    public void keepFirstExceptionWhenExceptionDuringEvaluateAndAfterTestMethod() throws Throwable {
        NullPointerException exception1 = new NullPointerException("expected");
        NullPointerException exception2 = new NullPointerException("expected");
        statementMock.raises(exception1).evaluate();
        unitilsTestListenerMock.raises(exception2).afterTestMethod(exception1);
        try {
            afterTestMethodStatement.evaluate();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertSame(exception1, e);
        }
    }
}
