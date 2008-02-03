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
package org.unitils.jpa.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p/>
 * Annotation indicating that this field or method should be initialized with the <code>EntityManagerFactory</code> object
 * that can be used to create <code>EntityManager</code> objects that provide a connection to the unit test database.
 * If a field is annotated, it should be of type <code>javax.persistence.EntityManagerFactory</code>. If a method is annotated,
 * the method should have following signature: void myMethod(javax.persistence.EntityManagerFactory entityManagerFactory)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface JpaEntityManagerFactory {

	/**
	 * The name of the persistence unit, defined in peristence.xml, that has to be used
	 */
	String persistenceUnit() default "";
	
	/**
	 * The persistence xml files that have to be loaded for configuring the EntityManagerFactory.
	 * If omitted, the default META-INF/persistence.xml file is loaded.
	 */
	String[] configFiles() default {};
    
}
