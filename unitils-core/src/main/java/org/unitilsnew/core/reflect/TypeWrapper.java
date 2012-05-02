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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapper {

    protected Type wrappedType;


    public TypeWrapper(Type wrappedType) {
        this.wrappedType = wrappedType;
    }

    public Type getWrappedType() {
        return wrappedType;
    }

    public String getName() {
        if (isClassType()) {
            return ((Class<?>) wrappedType).getName();
        }
        return wrappedType.toString();
    }

    public boolean isPrimitive() {
        if (!isClassType()) {
            return false;
        }
        return ((Class<?>) wrappedType).isPrimitive();
    }


    public boolean isClassType() {
        return wrappedType instanceof Class;
    }

    public boolean isParameterizedType() {
        return wrappedType instanceof ParameterizedType;
    }


    public boolean isOfType(Type type) {
        return wrappedType.equals(type);
    }

    public boolean isAssignableTo(Type type) {
        return isAssignable(type, wrappedType);
    }

    public boolean isAssignableFrom(Type type) {
        return isAssignable(wrappedType, type);
    }


    public Class<?> getSingleGenericClass() {
        Type genericType = getSingleGenericType();
        if (genericType instanceof ParameterizedType) {
            genericType = ((ParameterizedType) genericType).getRawType();
        }
        if (genericType instanceof Class) {
            return (Class<?>) genericType;
        }
        throw new UnitilsException("Unable to determine unique generic class for type: " + getName() + ".\n" +
                "Generic type is not a class: " + genericType);
    }

    public Type getSingleGenericType() {
        if (!(wrappedType instanceof ParameterizedType)) {
            throw new UnitilsException("Unable to determine unique generic type for type: " + getName() + ".\n" +
                    "Type is not a generic type.");
        }
        Type[] genericTypes = ((ParameterizedType) wrappedType).getActualTypeArguments();
        if (genericTypes.length == 0) {
            throw new UnitilsException("Unable to determine unique generic type for type: " + getName() + ".\n" +
                    "Type is not a generic type.");
        }
        if (genericTypes.length > 1) {
            throw new UnitilsException("Unable to determine unique generic type for type: " + getName() + ".\n" +
                    "The type declares more than one generic type: " + Arrays.toString(genericTypes));
        }
        return genericTypes[0];
    }


    /**
     * Checks whether the given fromType is assignable to the given toType, also
     * taking into account possible auto-boxing.
     *
     * @param toType   The to type, not null
     * @param fromType The from type, not null
     * @return True if assignable
     */
    protected boolean isAssignable(Type toType, Type fromType) {
        if (fromType == null || toType == null) {
            return false;
        }
        if (toType.equals(fromType)) {
            return true;
        }
        if (Object.class.equals(toType)) {
            return true;
        }
        if (fromType instanceof Class<?> && toType instanceof Class<?>) {
            return isAssignableClass((Class<?>) toType, (Class<?>) fromType);
        }
        if (fromType instanceof ParameterizedType && toType instanceof ParameterizedType) {
            return isAssignableParameterizedType((ParameterizedType) toType, (ParameterizedType) fromType);
        }
        if (toType instanceof WildcardType) {
            return isAssignableWildcardType((WildcardType) toType, fromType);
        }
        return false;
    }

    protected boolean isAssignableClass(Class<?> toClass, Class<?> fromClass) {
        // handle auto boxing types
        if (boolean.class.equals(fromClass) && Boolean.class.isAssignableFrom(toClass)
                || boolean.class.equals(toClass) && Boolean.class.isAssignableFrom(fromClass)) {
            return true;
        }
        if (char.class.equals(fromClass) && Character.class.isAssignableFrom(toClass) || char.class.equals(toClass)
                && Character.class.isAssignableFrom(fromClass)) {
            return true;
        }
        if (int.class.equals(fromClass) && Integer.class.isAssignableFrom(toClass) || int.class.equals(toClass)
                && Integer.class.isAssignableFrom(fromClass)) {
            return true;
        }
        if (long.class.equals(fromClass) && Long.class.isAssignableFrom(toClass) || long.class.equals(toClass)
                && Long.class.isAssignableFrom(fromClass)) {
            return true;
        }
        if (float.class.equals(fromClass) && Float.class.isAssignableFrom(toClass) || float.class.equals(toClass)
                && Float.class.isAssignableFrom(fromClass)) {
            return true;
        }
        if (double.class.equals(fromClass) && Double.class.isAssignableFrom(toClass)
                || double.class.equals(toClass) && Double.class.isAssignableFrom(fromClass)) {
            return true;
        }
        return toClass.isAssignableFrom(fromClass);
    }

    protected boolean isAssignableParameterizedType(ParameterizedType toType, ParameterizedType fromType) {
        Type[] toTypeArguments = toType.getActualTypeArguments();
        Type[] fromTypeArguments = fromType.getActualTypeArguments();
        if (toTypeArguments.length != fromTypeArguments.length) {
            return false;
        }
        for (int size = toTypeArguments.length, i = 0; i < size; ++i) {
            Type toTypeArgument = toTypeArguments[i];
            Type fromTypeArgument = fromTypeArguments[i];
            if (!toTypeArgument.equals(fromTypeArgument) &&
                    !(toTypeArgument instanceof WildcardType && isAssignableWildcardType((WildcardType) toTypeArgument, fromTypeArgument))) {
                return false;
            }
        }
        return true;
    }

    protected boolean isAssignableWildcardType(WildcardType toType, Type fromType) {
        Type[] upperBounds = toType.getUpperBounds();
        Type[] lowerBounds = toType.getLowerBounds();
        for (int size = upperBounds.length, i = 0; i < size; ++i) {
            if (!isAssignable(upperBounds[i], fromType)) {
                return false;
            }
        }
        for (int size = lowerBounds.length, i = 0; i < size; ++i) {
            if (!isAssignable(fromType, lowerBounds[i])) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object value) {
        if (this == value) {
            return true;
        }
        if (value == null || getClass() != value.getClass()) {
            return false;
        }

        TypeWrapper that = (TypeWrapper) value;
        if (wrappedType != null ? !wrappedType.equals(that.wrappedType) : that.wrappedType != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return wrappedType != null ? wrappedType.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
