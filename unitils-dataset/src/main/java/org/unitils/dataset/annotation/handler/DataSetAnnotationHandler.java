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
package org.unitils.dataset.annotation.handler;

import org.unitils.dataset.DataSetModule;

import java.lang.reflect.Method;

/**
 * Handles the operation of a data set annotation.
 * <br/><br/>
 * When unitils finds a test method or class that is annotated with a {@link org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation}
 * or {@link org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation} annotation, it will
 * create an instance of the annotation handler specified in the marker annotation and delegate the actual loading or
 * asserting to that handler.
 * <br/><br/>
 * If you want to implement your own data set annotation, you should first annotate it with one of the marker annotations
 * specifying your class as the handler class. You can then implement the operation by for example calling one of the
 * methods on the given data set module or by creating your own load or assert strategy and execute it.
 * <code><pre>
 * '@Target({TYPE, METHOD})
 * '@Retention(RUNTIME)
 * '@Inherited
 * '@MarkerForAssertDataSetAnnotation(MyDataSetAnnotationHandler.class)
 * 'public @interface MyDataSetAnnotation {
 * '}
 *
 * 'public class MyDataSetAnnotationHandler implements DataSetAnnotationHandler&lt;MyDataSetAnnotation&gt; {
 * '
 * '    public void handle(RefreshDataSet annotation, Method testMethod, Object testInstance, DataSetModule dataSetModule) {
 * '       // load the data set
 * '       dataSetModule.insertDataSet("table col=1");
 * '    }
 * '}
 * </pre></code>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @param <T>
 */
public interface DataSetAnnotationHandler<T> {

    /**
     * Handles the actual loading or asserting operation.
     *
     * @param annotation    The annotation that is being handled, not null
     * @param testMethod    The current test method, not null
     * @param testInstance  The current test instance, not null
     * @param dataSetModule A reference to the data set module, not null
     */
    void handle(T annotation, Method testMethod, Object testInstance, DataSetModule dataSetModule);
}