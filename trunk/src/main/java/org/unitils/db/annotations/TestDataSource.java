/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.db.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation indicating that this field or method should be initialized with the <code>DataSource</code> that supplies
 * a connection to the unit test database. If a field is annotated, it should be of type <code>DataSource</code>. If
 * a method is annotated, the method should have following signature: void myMethod(DataSource dataSource)
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestDataSource {
}
