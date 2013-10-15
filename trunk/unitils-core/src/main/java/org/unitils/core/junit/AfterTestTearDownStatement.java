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

import org.junit.runners.model.Statement;
import org.unitils.core.engine.UnitilsTestListener;

/**
 * @author Tim Ducheyne
 */
public class AfterTestTearDownStatement extends Statement {

    protected UnitilsTestListener unitilsTestListener;
    protected Statement nextStatement;


    public AfterTestTearDownStatement(UnitilsTestListener unitilsTestListener, Statement nextStatement) {
        this.unitilsTestListener = unitilsTestListener;
        this.nextStatement = nextStatement;
    }


    @Override
    public void evaluate() throws Throwable {
        nextStatement.evaluate();
        unitilsTestListener.afterTestTearDown();
    }
}
