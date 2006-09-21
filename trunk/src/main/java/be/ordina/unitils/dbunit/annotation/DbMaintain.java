package be.ordina.unitils.dbunit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo javadoc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbMaintain {

    boolean empty() default true;

    boolean update() default true;

    boolean disableConstraints() default true;

}
