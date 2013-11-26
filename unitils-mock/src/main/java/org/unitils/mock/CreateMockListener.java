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
package org.unitils.mock;

/**
 * Let your test class implement this interface when you want to be notified when a mock was created.
 * This could for example be used to  do some extra configuration or install the instance in a service locator.
 *
 * @author Tim Ducheyne
 */
public interface CreateMockListener {

    /**
     * * The passed object is .
     * The name is the name of the mock, typically the name of the field.
     * The type is the class type of the mocked instance.
     *
     * @param mock the mock instance, not null
     * @param name The name of the mock, not null
     * @param type The mocked type, not null
     */
    void mockCreated(Mock<?> mock, String name, Class<?> type);
}
