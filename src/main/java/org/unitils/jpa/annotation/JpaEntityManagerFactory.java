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
 * Annotation that can be used for configuring a JPA <code>EntityManagerFactory</code> on a test class. Such a 
 * <code>EntityManagerFactory</code> will connect to the unitils configured test datasource. 
 * <p/>
 * This annotation can be used at class, method or field level. If at field level, the <code>EntityManagerFactory</code>
 * associated with this test object is injected. If put on a method with a single argument of type <code>EntityManagerFactory</code>,
 * the method is invoked with the <code>EntityManagerFactory</code> as argument.
 * <p/>
 * This annotation can also be used to identify a custom configuration method. Such a method takes as single parameter a 
 * spring <code>org.springframework.orm.jpa.AbstractEntityManagerFactoryBean</code> object, on which any specified 
 * configuration file was already loaded.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface JpaEntityManagerFactory {

	/**
	 * The name of the persistence unit, defined in the specific persistence config file
	 */
	String persistenceUnit() default "";
	
	/**
	 * The persistence xml file that has to be loaded for configuring the EntityManagerFactory.
	 * If omitted, the default META-INF/persistence.xml file is loaded.
	 */
	String configFile() default "";
    
}
