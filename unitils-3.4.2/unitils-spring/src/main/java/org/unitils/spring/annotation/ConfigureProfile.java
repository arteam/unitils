package org.unitils.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.unitils.spring.profile.TypeConfiguration;


/**
 * Choose which Spring profile you want to use.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConfigureProfile {
    String value() default "";
    String packageProfile() default "";
    TypeConfiguration configuration() default TypeConfiguration.APPLICATIONCONTEXT;
}
