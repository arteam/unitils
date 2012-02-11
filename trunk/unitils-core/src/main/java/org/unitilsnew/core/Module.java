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

package org.unitilsnew.core;

import org.unitilsnew.core.listener.FieldAnnotationListener;
import org.unitilsnew.core.listener.TestAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.util.List;

/**
 * @author Tim Ducheyne
 */
public abstract class Module {

    public List<Class<? extends TestListener>> getTestListenerTypes() {
        return null;
    }

    public List<Class<? extends FieldAnnotationListener<?>>> getFieldAnnotationListenerTypes() {
        return null;
    }

    public List<Class<? extends TestAnnotationListener<?>>> getTestAnnotationListenerTypes() {
        return null;
    }

    public List<Class<?>> getFacadeTypes() {
        return null;
    }
}
