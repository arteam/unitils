/*
 * Copyright 2012,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitilsnew.core.reflect;

import org.unitils.core.UnitilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapper extends TypeWrapper {

    protected Class<?> wrappedClass;


    public ClassWrapper(Class<?> wrappedClass) {
        super(wrappedClass);
        this.wrappedClass = wrappedClass;
    }


    public Class<?> getWrappedClass() {
        return wrappedClass;
    }


    public List<FieldWrapper> getFields() {
        List<FieldWrapper> fields = new ArrayList<FieldWrapper>();
        addFields(wrappedClass, false, fields);
        return fields;
    }

    public List<FieldWrapper> getStaticFields() {
        List<FieldWrapper> fields = new ArrayList<FieldWrapper>();
        addFields(wrappedClass, true, fields);
        return fields;
    }


    public FieldWrapper getField(String name) {
        if (isBlank(name)) {
            throw new UnitilsException("Unable to get field. Name cannot be null or empty.");
        }
        Field field = getSimpleField(name, wrappedClass);
        if (field == null) {
            throw new UnitilsException("Unable to get field with name '" + name + "'. No such field exists on class " + wrappedClass.getName() + " or one of its superclasses.");
        }
        return new FieldWrapper(field);
    }

    public List<FieldWrapper> getFields(List<String> names) {
        List<FieldWrapper> fieldWrappers = new ArrayList<FieldWrapper>();
        for (String name : names) {
            FieldWrapper fieldWrapper = getField(name);
            fieldWrappers.add(fieldWrapper);
        }
        return fieldWrappers;
    }

    public CompositeFieldWrapper getCompositeField(String property) {
        if (isBlank(property)) {
            throw new UnitilsException("Unable to get field. Property cannot be null or empty.");
        }
        List<Field> fields = new ArrayList<Field>();
        String[] names = property.split("\\.", -1);

        Class<?> currentClass = wrappedClass;
        for (String name : names) {
            if (isBlank(name)) {
                throw new UnitilsException("Invalid property expression '" + property + "'. Make sure the expression follows following pattern: field1(.field2.(field3)).");
            }
            Field field = getSimpleField(name, currentClass);
            if (field == null) {
                throw new UnitilsException("Unable to get field for property '" + property + "'. Field with name '" + name + "' does not exist on class " + currentClass.getName() + " or one of its superclasses.");
            }
            fields.add(field);
            currentClass = field.getType();
        }
        return new CompositeFieldWrapper(fields);
    }

    /**
     * Gets the non-static fields in this class or superclass that have the exact given type.
     *
     * @param type The type, not null
     * @return The fields, empty if none found
     */
    public List<FieldWrapper> getFieldsOfType(Type type) {
        if (type == null) {
            throw new UnitilsException("Unable to get fields of type. Type cannot be null.");
        }
        List<FieldWrapper> fields = getFields();
        return getFieldsOfType(type, fields);
    }

    /**
     * Gets the static fields in this class or superclass that have the exact given type.
     *
     * @param type The type, not null
     * @return The fields, empty if none found
     */
    public List<FieldWrapper> getStaticFieldsOfType(Type type) {
        if (type == null) {
            throw new UnitilsException("Unable to get static fields of type. Type cannot be null.");
        }
        List<FieldWrapper> fields = getStaticFields();
        return getFieldsOfType(type, fields);
    }


    /**
     * Gets the non-static fields in this class or superclass that have a type that is assignable from the given type.
     *
     * @param type The type, not null
     * @return The fields, empty if none found
     */
    public List<FieldWrapper> getFieldsAssignableFrom(Type type) {
        if (type == null) {
            throw new UnitilsException("Unable to get fields assignable from type. Type cannot be null.");
        }
        List<FieldWrapper> fields = getFields();
        return getFieldsAssignableFrom(type, fields);
    }

    /**
     * Gets the static fields in this class or superclass that have a type that is assignable from the given type.
     *
     * @param type The type, not null
     * @return The fields, empty if none found
     */
    public List<FieldWrapper> getStaticFieldsAssignableFrom(Type type) {
        if (type == null) {
            throw new UnitilsException("Unable to get static fields assignable from type. Type cannot be null.");
        }
        List<FieldWrapper> fields = getStaticFields();
        return getFieldsAssignableFrom(type, fields);
    }


    // note: gets all methods => overridden methods are returned twice
    public List<Method> getMethods() {
        List<Method> methods = new ArrayList<Method>();
        addMethods(wrappedClass, methods);
        return methods;
    }


    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass) {
        List<A> result = new ArrayList<A>(3);

        List<Annotation> annotations = getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                result.add((A) annotation);
            }
        }
        return result;
    }

    public List<Annotation> getAnnotations() {
        List<Annotation> annotations = new ArrayList<Annotation>(3);
        addAnnotations(wrappedClass, annotations);
        return annotations;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        List<Annotation> annotations = getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates an instance of this class using the default (no-arg) constructor.
     * An exception is raised when there is no such constructor
     *
     * @return An instance of this type, not null
     */
    public Object createInstance() {
        if (wrappedClass.isMemberClass() && !isStatic(wrappedClass.getModifiers())) {
            throw new UnitilsException("Unable to create instance of type " + getName() + ". Type is a non-static inner class which is only know in the context of an instance of the enclosing class. Declare the inner class static to make construction possible.");
        }
        try {
            Constructor<?> constructor = wrappedClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Unable to create instance of type " + getName() + ". No default (no-argument) constructor found.", e);


        } catch (InstantiationException e) {
            throw new UnitilsException("Unable to create instance of type " + getName() + ". Type is an abstract class.", e);

        } catch (Exception e) {
            Throwable cause = (e instanceof InvocationTargetException) ? e.getCause() : e;
            String reason = cause == null ? "" : " Reason: " + cause.toString();
            throw new UnitilsException("Unable to create instance of type " + getName() + "." + reason, cause);
        }
    }


    protected void addFields(Class<?> clazz, boolean staticFields, List<FieldWrapper> fields) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Field[] classFields = clazz.getDeclaredFields();
        for (Field field : classFields) {
            // exclude static and special fields
            if (!field.isSynthetic() && staticFields == isStatic(field.getModifiers())) {
                FieldWrapper fieldWrapper = new FieldWrapper(field);
                fields.add(fieldWrapper);
            }
        }
        addFields(clazz.getSuperclass(), staticFields, fields);
    }

    protected void addMethods(Class<?> clazz, List<Method> methods) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Method[] classMethods = clazz.getDeclaredMethods();
        for (Method method : classMethods) {
            // exclude special methods
            if (!method.isSynthetic() && !method.isBridge() && !isStatic(method.getModifiers())) {
                methods.add(method);
            }
        }
        addMethods(clazz.getSuperclass(), methods);
    }

    protected void addAnnotations(Class<?> clazz, List<Annotation> classAnnotations) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        classAnnotations.addAll(asList(annotations));
        addAnnotations(clazz.getSuperclass(), classAnnotations);
    }

    protected Field getSimpleField(String name, Class<?> clazz) {
        Class<?> currentClass = clazz;
        while (!Object.class.equals(currentClass)) {
            try {
                String trimmedName = name.trim();
                return currentClass.getDeclaredField(trimmedName);
            } catch (NoSuchFieldException e) {
                // not found, try the superclass
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    protected List<FieldWrapper> getFieldsOfType(Type type, List<FieldWrapper> fields) {
        List<FieldWrapper> result = new ArrayList<FieldWrapper>();
        for (FieldWrapper field : fields) {
            if (field.isOfType(type)) {
                result.add(field);
            }
        }
        return result;
    }

    protected List<FieldWrapper> getFieldsAssignableFrom(Type type, List<FieldWrapper> fields) {
        List<FieldWrapper> result = new ArrayList<FieldWrapper>();
        for (FieldWrapper field : fields) {
            if (field.isAssignableFrom(type)) {
                result.add(field);
            }
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassWrapper classWrapper1 = (ClassWrapper) o;
        if (wrappedClass != null ? !wrappedClass.equals(classWrapper1.wrappedClass) : classWrapper1.wrappedClass != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return wrappedClass != null ? wrappedClass.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
