package org.unitils.db.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation indicating that this method should be executed after the <code>DataSource</code> has been created. The
 * annotated method should have following signature: void myMethod(DataSource dataSource)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterCreateDataSource {
}
