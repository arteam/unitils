/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.inject.util;

/**
 * Class for holding values that need to be restored after a test was performed. It also contains information
 * on how and where the value needs to be restored.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ValueToRestore {

    /* The target class on which to restore the value */
    private Class targetClass;

    /* The OGNL expression indicating where to inject the restored value, null for auto-injection */
    private String property;

    /* The type of the field in which to restore the value */
    private Class fieldType;

    /* In case auto-injection is to be used, this should hold the access type (field or setter) */
    private PropertyAccess propertyAccess;

    /* The value to restore */
    private Object value;


    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public PropertyAccess getPropertyAccessType() {
        return propertyAccess;
    }

    public void setPropertyAccessType(PropertyAccess propertyAccess) {
        this.propertyAccess = propertyAccess;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
