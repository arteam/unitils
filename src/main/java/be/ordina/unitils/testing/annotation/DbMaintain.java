package be.ordina.unitils.testing.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
