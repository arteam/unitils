/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.hibernate.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * todo javadoc
 *
 * Annotation indicating that this field or method should be initialized with the Hibernate<code>SessionFactory</code> object
 * that can be used to create Hibernate <code>Session</code> object that provide a connection to the unit test database.
 * If a field is annotated, it should be of type <code>org.hibernate.SessionFactory</code>. If a method is annotated,
 * the method should have following signature: void myMethod(org.hibernate.SessionFactory sessionFactory)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface HibernateSessionFactory {
}
