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
package org.unitils.orm.jpa.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used in three different ways: 
 * <ul><li>
 * If the persistenceUnit and optionally the configFile attributes are specified, its goal is to specify 
 * <code>EntityManagerFactory</code> configuration parameters. 
 * </li><li>
 * If these attributes are not specified and the annotation is put on a field or type <code>EntityManagerFactory</code> 
 * or a method that takes a single parameter  of type <code>EnitityManagerFactory</code>, the <code>EntityManagerFactory</code> 
 * for this test object is injected into this field or method. 
 * </li><li>
 * If put on a method that takes a single <code>org.springframework.orm.jpa.AbstractEntityManagerFactoryBean</code> 
 * parameter, this method becomes a custom configuration method for this <code>EntityManagerFactory</code>. This method
 * will be invoked when creating a new <code>EntityManagerFactory</code>, after having loaded all other configuration
 * on this factory bean.
 * </li></ul>
 * The configured <code>EntityManagerFactory</code> will connect to the unitils configured test datasource, and
 * will join in unitils test-bound transactions.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface JpaEntityManagerFactory {

	/**
	 * The name of the persistence unit, as defined in the persistence config file
	 */
	String persistenceUnit() default "";
	
	/**
	 * The persistence xml file that has to be loaded for configuring the EntityManagerFactory.
	 * If omitted, the default META-INF/persistence.xml file is loaded.
	 */
	String configFile() default "";
    
}
