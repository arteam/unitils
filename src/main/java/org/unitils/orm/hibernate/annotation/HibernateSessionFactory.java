/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.orm.hibernate.annotation;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation that can be used for configuring a hibernate <code>SessionFactory</code> on a test class. Such a 
 * <code>SessionFactory</code> will connect to the unitils configured test datasource. 
 * <p/>
 * This annotation can be used at class, method or field level. If at field level, the <code>SessionFactory</code>
 * associated with this test object is injected. If put on a method with a single argument of type <code>SessionFactory</code>,
 * the method is invoked with the <code>SessionFactory</code> as argument.
 * <p/>
 * This annotation can also be used to identify a custom configuration method. Such a method takes as single parameter a 
 * hibernate <code>org.hibernate.cfg.Configuration</code> object, on which any specified  configuration files were 
 * already loaded.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface HibernateSessionFactory {

    /**
     * Specifies zero, one or more configuration files, that will be used to configure the hibernate <code>SessionFactory</code>
     */
    String[] value() default {};

}
