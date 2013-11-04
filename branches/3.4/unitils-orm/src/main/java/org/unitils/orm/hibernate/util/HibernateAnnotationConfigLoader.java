package org.unitils.orm.hibernate.util;

import java.lang.reflect.Method;

import org.hibernate.cfg.Configuration;
import org.unitils.orm.common.util.AnnotationConfigLoader;
import org.unitils.orm.common.util.OrmConfig;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.util.CollectionUtils;

public class HibernateAnnotationConfigLoader extends AnnotationConfigLoader<HibernateSessionFactory, OrmConfig> {

	public HibernateAnnotationConfigLoader() {
		super(HibernateSessionFactory.class);
	}


	protected boolean isConfiguringAnnotation(HibernateSessionFactory annotation) {
		return annotation.value().length > 0;
	}


	protected OrmConfig createResourceConfig(HibernateSessionFactory configuringAnnotation, Method customConfigMethod) {
		return new OrmConfig(CollectionUtils.asSet(configuringAnnotation.value()), customConfigMethod);
	}


	protected boolean isCustomConfigMethod(Method annotatedMethod) {
		return annotatedMethod.getReturnType().toString().equals("void")
				&& annotatedMethod.getParameterTypes().length == 1 
				&& Configuration.class.isAssignableFrom(annotatedMethod.getParameterTypes()[0]);
	}
}
