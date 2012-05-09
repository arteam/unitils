package org.unitils.io;

import org.unitils.util.ReflectionUtils;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.core.reflect.ClassWrapper;
import org.unitilsnew.core.reflect.FieldWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class FieldAnnotationListenerTestableAdapter<T extends Annotation> {

    private FieldAnnotationListener<T> listener;


    public FieldAnnotationListenerTestableAdapter(FieldAnnotationListener<T> listener) {
        this.listener = listener;
    }

    public void beforeTestSetUp(Object testObject, String testMethodName, String fieldName, Annotations<T> annotations) {
        TestInstance testInstance = createTestInstance(testObject, testMethodName);
        FieldWrapper fieldWrapper = new FieldWrapper(ReflectionUtils.getFieldWithName(testObject.getClass(), fieldName, false));
        TestField testField = new TestField(fieldWrapper, testObject);

        listener.beforeTestSetUp(testInstance, testField, annotations);
    }

    public void beforeTestMethod(Object testObject, String testMethodName, String fieldName, Annotations<T> annotations) {
        TestInstance testInstance = createTestInstance(testObject, testMethodName);
        FieldWrapper fieldWrapper = new FieldWrapper(ReflectionUtils.getFieldWithName(testObject.getClass(), fieldName, false));
        TestField testField = new TestField(fieldWrapper, testObject);

        listener.beforeTestMethod(testInstance, testField, annotations);
    }

    public void afterTestMethod(Object testObject, String testMethodName, String fieldName, Throwable testThrowable, Annotations<T> annotations) {
        TestInstance testInstance = createTestInstance(testObject, testMethodName);
        FieldWrapper fieldWrapper = new FieldWrapper(ReflectionUtils.getFieldWithName(testObject.getClass(), fieldName, false));
        TestField testField = new TestField(fieldWrapper, testObject);

        listener.afterTestMethod(testInstance, testField, annotations, testThrowable);
    }

    public void afterTestTearDown(Object testObject, String testMethodName, String fieldName, Annotations<T> annotations) {
        TestInstance testInstance = createTestInstance(testObject, testMethodName);
        FieldWrapper fieldWrapper = new FieldWrapper(ReflectionUtils.getFieldWithName(testObject.getClass(), fieldName, false));
        TestField testField = new TestField(fieldWrapper, testObject);

        listener.afterTestTearDown(testInstance, testField, annotations);
    }

    protected TestInstance createTestInstance(Object testObject, String testMethodName) {
        ClassWrapper testClass = new ClassWrapper(testObject.getClass());

        Method testMethod = ReflectionUtils.getMethod(testObject.getClass(), testMethodName, false);
        TestInstance testInstance = new TestInstance(testClass, testObject, testMethod);

        return testInstance;
    }

}
