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

package org.unitils.inject.core;

import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.TestedObject;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TargetService {


    public List<?> getTargetsForInjection(List<String> targetNames, TestInstance testInstance) {
        List<?> targets;
        try {
            List<TestField> targetTestFields = getTargetTestFields(targetNames, testInstance);
            targets = getTargets(targetTestFields);
        } catch (Exception e) {
            throw new UnitilsException("Unable to get targets for injection. Reason:\n" + e.getMessage(), e);
        }
        if (targets.isEmpty()) {
            throw new UnitilsException("No targets for injection found.\n" +
                    "The targets should either be specified explicitly using the target property or by annotating the target fields using the @" + TestedObject.class.getSimpleName() + " annotation.");
        }
        return targets;
    }


    protected List<TestField> getTargetTestFields(List<String> targetNames, TestInstance testInstance) {
        if (targetNames == null || targetNames.size() == 0) {
            return testInstance.getTestFieldsWithAnnotation(TestedObject.class);
        }
        return testInstance.getTestFields(targetNames);
    }

    protected List<?> getTargets(List<TestField> targetTestFields) {
        if (targetTestFields.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> targets = new ArrayList<Object>(targetTestFields.size());
        for (TestField targetTestField : targetTestFields) {
            Object target = targetTestField.getValue();
            targets.add(target);
        }
        return targets;
    }
}
