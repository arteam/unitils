package org.unitils.util;

import java.lang.annotation.Annotation;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface AnnotationPropertyAccessor<S extends Annotation, T> {

    public T getAnnotationProperty(S annotation);
}
