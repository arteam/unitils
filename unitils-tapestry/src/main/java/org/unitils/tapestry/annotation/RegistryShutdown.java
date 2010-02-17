package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If there is a method annotated with this annotation, the method is used to shutodwn the Tapestry
 * registry. 
 * 
 * The method must be non static.
 * 
 * The following method signatures are applicable:
 * <ul>
 * <li>void method(Registry registry)</li>
 * </ul>
 *  
 * An annotated method defined in a sub class is preferred to a method defined in a super class.
 * 
 * As an alternative {@link TapestryRegistry#registryShutdownMethodName()} can be used.
 * 
 * The registry factory method is resolved using the following rules: 
 * <ol>
 * <li>Use the method defined by {@link TapestryRegistry#registryShutdownMethodName()} if specified</li>
 * <li>Use the method annotated with {@link RegistryShutdown} if available</li>
 * <li>Let Unitils take care of creating the registry</li>
 * </ol>
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryShutdown {

}
