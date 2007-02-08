package org.unitils.spring.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * todo javadoc
 * <p/>
 * Annotation indicating that this field or method should be initialized with the Hibernate<code>SessionFactory</code> object
 * that can be used to create Hibernate <code>Session</code> object that provide a connection to the unit test database.
 * If a field is annotated, it should be of type <code>org.hibernate.SessionFactory</code>. If a method is annotated,
 * the method should have following signature: void myMethod(org.hibernate.SessionFactory sessionFactory)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringHibernateSessionFactory {

    //todo javadoc

    String[] value() default {};

}
