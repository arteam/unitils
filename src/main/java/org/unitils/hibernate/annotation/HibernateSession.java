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
 * Annotation indicating that this field or method should be initialized with the Hibernate<code>Session</code> object
 * that connects to the unit test database. If a field is annotated, it should be of type <code>org.hibernate.Session</code>.
 * If a method is annotated, the method should have following signature: void myMethod(org.hibernate.Session session)
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HibernateSession {
}
