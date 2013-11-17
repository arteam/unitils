package org.unitils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.MethodValidator;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;


/**
 * Parameterized runner.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class UnitilsParameterized extends Suite {
    private Class<?> clazz;
    private static final Log LOGGER = LogFactory.getLog(UnitilsParameterized.class);
    private static final String METHOD = "Method ";
    /**
     * 
     * TestClassRunnerForParameters.
     * 
     * @author wiw
     * 
     * @since 
     *
     */
    protected class TestClassRunnerForParameters extends UnitilsJUnit4TestClassRunner {

        private final int fParameterSetNumber;

        private final List<Object[]> fParameterList;

        private org.junit.internal.runners.TestClass testClassInternalRunners;
        /**
         * @param javaClass
         * @param parametersList
         * @param i
         * @throws Exception 
         */
        public TestClassRunnerForParameters(Class<?> javaClass, List<Object[]> parametersList, int i) throws Exception {
            super(javaClass);
            this.fParameterList = parametersList;
            this.fParameterSetNumber = i;
            this.testClassInternalRunners = new org.junit.internal.runners.TestClass(javaClass);
        }
        

        /**
         * @see org.junit.internal.runners.JUnit4ClassRunner#createTest()
         */
        @Override
        protected Object createTest() throws Exception {
            return getfTestClass().getOnlyConstructor().newInstance(computeParams());
        }

        /**
         * @return
         */
        private TestClass getfTestClass() {
            return  new TestClass(clazz);
        }

        /**
         * @return
         * @throws Exception 
         */
        protected Object[] computeParams() throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(String.format("%s.%s() must return a Collection of arrays.", getTestClass().getName(), getParametersMethod(getfTestClass()).getName()));
            }
        }
        /**
         * @see org.junit.internal.runners.JUnit4ClassRunner#getName()
         */
        @Override
        protected String getName() {
            StringBuffer name = new StringBuffer();
            try {
                Object[] data = fParameterList.get(fParameterSetNumber);
                for (Object object : data) {
                    if (object != null) {
                        name.append(object.toString());
                        name.append(",");
                    } else {
                        name.append("null,");
                    }

                }
            } catch (IndexOutOfBoundsException e) {
                LOGGER.error(e.getMessage(), e);
            }
            name = new StringBuffer(name.substring(0, name.length() - 1));

            return String.format("dataset [%s]", name.toString());
        }

        /**
         * @see org.junit.internal.runners.JUnit4ClassRunner#testName(java.lang.reflect.Method)
         */
        @Override
        protected String testName(Method method) {
            return String.format("%s[%s]", method.getName(), fParameterSetNumber);
        }

        /**
         * @see org.junit.internal.runners.JUnit4ClassRunner#validate()
         */
        @Override
        protected void validate() throws InitializationError {
            testClassInternalRunners = new org.junit.internal.runners.TestClass(clazz);
            UnitilsMethodValidator validator = new UnitilsMethodValidator(testClassInternalRunners);
            List<Throwable> errors = validator.validateMethodsForParameterizedRunner();
            if (!errors.isEmpty()) {
                throw new InitializationError(errors);
            }
        }
        
    }
    private final List<Runner> runners= new ArrayList<Runner>();

    

    /**
     * Only called reflectively. Do not use programmatically.
     * @param klass 
     * @throws Throwable 
     */
    public UnitilsParameterized(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        this.clazz = klass;
        List<Object[]> parametersList = getParametersList(getTestClass());
        for (int i= 0; i < parametersList.size(); i++) {
            runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(), parametersList, i));

        }
    }

    /**
     * @param testClass
     * @return {@link List}
     * @throws Throwable 
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList(TestClass testClass) throws Exception, Throwable {
        return (List<Object[]>) getParametersMethod(testClass).invokeExplosively(null);
    }

    /**
     * @param testClass
     * @return
     */
    protected FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
        
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }

        }

        throw new Exception("No public static parameters method on class " + testClass.getName());
    }
    /**
     * @see org.junit.runners.Suite#getChildren()
     */
    @Override
    protected List<Runner> getChildren() {
        return Collections.unmodifiableList(runners);
    }
    /**
     * 
     * UnitilsMethodValidator.
     * 
     * @author wiw
     * 
     * @since 
     *
     */
    protected static class UnitilsMethodValidator extends MethodValidator {

        private org.junit.internal.runners.TestClass testclass;
        private List<Throwable> errors = new ArrayList<Throwable>();
        /**
         * @param testClass
         */
        public UnitilsMethodValidator(org.junit.internal.runners.TestClass testClass) {
            super(testClass);
            this.testclass = testClass;
        }
        public List<Throwable> validateMethodsForParameterizedRunner() {
            validateArgConstructor();
            validateStaticMethods();
            validateInstanceMethods();

            return errors;
        }   



        //private methods
        protected void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic) {
            List<Method> methods= testclass.getAnnotatedMethods(annotation);

            for (Method each : methods) {
                if (Modifier.isStatic(each.getModifiers()) != isStatic) {
                    String state= isStatic ? "should" : "should not";
                    errors.add(new Exception(METHOD + each.getName() + "() "
                        + state + " be static"));
                }
                if (!Modifier.isPublic(each.getDeclaringClass().getModifiers())) {
                    errors.add(new Exception("Class " + each.getDeclaringClass().getName() + " should be public"));
                }
                if (!Modifier.isPublic(each.getModifiers())) {
                    errors.add(new Exception(METHOD + each.getName() + " should be public"));
                }
                if (each.getReturnType() != Void.TYPE) {
                    errors.add(new Exception(METHOD + each.getName() + " should be void"));
                }
                if (each.getParameterTypes().length != 0) {
                    errors.add(new Exception(METHOD + each.getName()  + " should have no parameters"));
                }
            }
        }

        /**
         * @see org.junit.internal.runners.MethodValidator#validateInstanceMethods()
         */
        @Override
        public void validateInstanceMethods() {
            validateTestMethods(After.class, false);
            validateTestMethods(Before.class, false);
            validateTestMethods(Test.class, false);

            List<Method> methods= testclass.getAnnotatedMethods(Test.class);
            if (methods.size() == 0) {
                errors.add(new Exception("No runnable methods"));
            }
        }

        /**
         * @see org.junit.internal.runners.MethodValidator#validateStaticMethods()
         */
        @Override
        public void validateStaticMethods() {
            validateTestMethods(BeforeClass.class, true);
            validateTestMethods(AfterClass.class, true);
        }
        public void validateArgConstructor() {
            org.junit.runners.model.TestClass clazz = new org.junit.runners.model.TestClass(testclass.getJavaClass());
            Constructor<?> onlyConstructor = clazz.getOnlyConstructor();
            
            if (onlyConstructor.getParameterTypes().length == 0) {
                errors.add(new Exception("Test class shouldn't have a public zero-argument constructor"));
            }

        }


        /**
         * @return the errors
         */
        protected List<Throwable> getErrors() {
            return errors;
        }
        
        
    }
}
