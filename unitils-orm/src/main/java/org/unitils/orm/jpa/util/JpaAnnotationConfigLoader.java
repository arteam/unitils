/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.orm.jpa.util;

import java.lang.reflect.Method;

import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.unitils.orm.common.util.AnnotationConfigLoader;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;


/**
 * {@link AnnotationConfigLoader} that looks an <code>EntityManagerFactory</code> configured using
 * {@link JpaEntityManagerFactory} anntations.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaAnnotationConfigLoader extends AnnotationConfigLoader<JpaEntityManagerFactory, JpaConfig> {

	
	public JpaAnnotationConfigLoader() {
		super(JpaEntityManagerFactory.class);
	}


	protected boolean isConfiguringAnnotation(JpaEntityManagerFactory annotation) {
		return !"".equals(annotation.persistenceUnit());
	}


	protected JpaConfig createResourceConfig(JpaEntityManagerFactory configuringAnnotation, Method customConfigMethod) {
		return new JpaConfig(configuringAnnotation.persistenceUnit(), configuringAnnotation.configFile(), customConfigMethod);
	}


	protected boolean isCustomConfigMethod(Method annotatedMethod) {
		return annotatedMethod.getReturnType().toString().equals("void")
				&& annotatedMethod.getParameterTypes().length == 1 
				&& AbstractEntityManagerFactoryBean.class.isAssignableFrom(annotatedMethod.getParameterTypes()[0]);
	}

}
