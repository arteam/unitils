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
package org.unitils.database.annotation;

import org.unitils.database.listener.TestDataSourceFieldAnnotationListener;
import org.unitilsnew.core.annotation.FieldAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation indicating that this field or method should be initialized with the <code>DataSource</code> that supplies
 * a connection to the unit test database.<br/>
 * If a field is annotated, it should be of type <code>DataSource</code>. This field can be private. Example:
 * <pre><code>
 * '    @DataSource
 *      private DataSource dataSource;
 * </code></pre>
 * If a method is annotated, the method should have 1 DataSource argument. Example:
 * <pre><code>
 * '    @DataSource
 *      void myMethod(DataSource dataSource)
 * </code></pre>
 * If there is more than 1 data source, you can specify the name of the database if you are configuring unitils
 * from properties or the id/name of the spring bean if you are using spring.<br/>
 * <b>NOTE</b>: you cannot use the {@link org.unitils.database.annotation.Transactional} annotation when there is more than 1 data source.
 * <br/><br/>
 * <i>Configuration in properties</i>: if the updateDataBaseSchema.enabled property is set to true, the test database
 * schema will automatically be updated using DbMaintain if needed. This check will be performed only once,
 * when the data source is created.
 * <i>Configuration in spring</i>: if there is a DbMaintain UpdateDatabaseTask bean configured in the
 * test application context, it will automatically be called to update the database. This will only be done
 * once per instance of the UpdateDatabaseTask bean.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target(FIELD)
@Retention(RUNTIME)
@FieldAnnotation(TestDataSourceFieldAnnotationListener.class)
public @interface TestDataSource {

    /**
     * @return The database name, empty string for the default database
     */
    String value() default "";

}
