package org.unitils.dbunit.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Multiple {@link ExpectedDataSet}.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface ExpectedDataSets {

    /**
     * One or more {@link ExpectedDataSet}
     * @return {@link java.lang.reflect.Array}
     */
    ExpectedDataSet[] value();
}
