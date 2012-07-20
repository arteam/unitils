/*
 * Copyright 2012,  Unitils.org
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
package org.unitilsnew.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

/**
 * @author Tim Ducheyne
 */
public class SpringTestManager {

    protected SpringTestContextWrapper springTestContextWrapper;


    public boolean isSpringTest() {
        return springTestContextWrapper != null;
    }

    public boolean isTestWithApplicationContext() {
        return springTestContextWrapper != null && springTestContextWrapper.getApplicationContext() != null;
    }

    public ApplicationContext getApplicationContext() {
        if (springTestContextWrapper == null) {
            return null;
        }
        return springTestContextWrapper.getApplicationContext();
    }

    public SpringTestContextWrapper getSpringTestContextWrapper() {
        return springTestContextWrapper;
    }


    public void setSpringTestContext(TestContext testContext) {
        if (testContext == null) {
            springTestContextWrapper = null;
            return;
        }
        springTestContextWrapper = new SpringTestContextWrapper(testContext);
    }

    public void reset() {
        setSpringTestContext(null);
    }
}
