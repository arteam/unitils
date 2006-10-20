/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.hibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that this method should be executed after a the Hibernate <code>org.hibernate.cfg.Configuration</code>
 * has been created. Such a method can perform some programmatic Hibernate configuration, such as registering mapped
 * classes. The annotated method should have following signature: void myMethod(org.hibernate.cfg.Configuration conn)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HibernateConfiguration {
}
