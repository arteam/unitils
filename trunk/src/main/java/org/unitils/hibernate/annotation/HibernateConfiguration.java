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
 * Annotation indicating that this method should be executed after the Hibernate <code>org.hibernate.cfg.Configuration</code>
 * has been created, but before the Hibernate <code>SessionFactory</code> is instantiated. Annotated methods can perform
 * programmatic Hibernate configuration, such as registering mapped classes. Annotated methods should have following
 * signature: void myMethod(org.hibernate.cfg.Configuration configuration)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HibernateConfiguration {
}
