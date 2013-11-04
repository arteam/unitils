package org.unitils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClass;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized.Parameters;

/**
 * A parameterised runner for Unitils.
 * 
 * @author Jef Verelst
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * 
 * @since 3.4
 * 
 */
public class UnitilsParameterized extends CompositeRunner {
    private TestClass testClass;
    /**
     * @param name
     * @throws Exception 
     */
    public UnitilsParameterized(TestClass testClass) throws Exception {
        super(testClass.getName());
        this.testClass = testClass;
        validateMethod();

        int i= 0;
        for (final Object each : getParametersList()) {
            if (each instanceof Object[])
                add(new TestClassRunnerForParameters(testClass.getClass(), (Object[])each, i++));
            else
                throw new Exception(String.format("%s.%s() must return a Collection of arrays.", this.testClass.getName(), getParametersMethod().getName()));
        }
    }

    /**
     * Validates the methods of the {@link TestClass}
     * @throws InitializationError
     */
    protected void validateMethod() throws InitializationError {
        MethodValidator methodValidator= new MethodValidator(testClass);
        methodValidator.validateStaticMethods();
        methodValidator.validateInstanceMethods();
        methodValidator.assertValid();
    }

    /**
     * Invokes all the methods with {@link Parameters}
     * @return {@link Collection}
     * @throws Exception
     */
    protected Collection<?> getParametersList() throws Exception {
        return (Collection<?>) getParametersMethod().invoke(null);
    }

    /**
     * This method gets the first method with the annotation {@link Parameters} and checks if it is a public and a static method.
     * @return {@link Method}
     * @throws Exception 
     */
    protected Method getParametersMethod() throws Exception {
        List<Method> methods= testClass.getAnnotatedMethods(Parameters.class);
        for (Method each : methods) {
            int modifiers= each.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
                return each;
        }

        throw new Exception("No public static parameters method on class " + getName());
    }


    protected static class TestClassRunnerForParameters extends UnitilsJUnit4TestClassRunner {
        /**The arrays also contains arrays. Each array is a collection of parameters for 1 test run.*/
        private final Object[] arrParameters;

        /**Defines which array of the array arrParameters is used.  */
        private final int fParameterSetNumber;

        private final Constructor<?> constructor;


        /**
         * @param testClass
         * @param parameters 
         * @param i 
         * @throws InitializationError
         */
        public TestClassRunnerForParameters(Class<?> testClass, Object[] parameters, int i) throws InitializationError {
            super(testClass);
            arrParameters = parameters;
            fParameterSetNumber = i;
            constructor = getOnlyConstructor();
        }


        /**
         * Checks if their is only one constructor.
         * @return {@link Constructor}
         */
        protected Constructor<?> getOnlyConstructor() {
            Constructor<?>[] constructors= getTestClass().getJavaClass().getConstructors();
            Assert.assertEquals(1, constructors.length);
            return constructors[0];
        }

        /**
         * Creates a new test with the correct array of parameters.
         * @see org.junit.internal.runners.JUnit4ClassRunner#createTest()
         */
        @Override
        protected Object createTest() throws Exception {
            return constructor.newInstance(computeParams());
        }
        
        protected Object[] computeParams() throws Exception {
            try {
                
                return (Object[]) arrParameters[fParameterSetNumber];
            } catch (ClassCastException e) {
                throw new Exception(String.format("%s must return a Collection of arrays.", getTestClass().getName()));
            }
        }

        @Override
        protected String getName() {
            return String.format("[%s]", fParameterSetNumber);
        }

        /**
         * name of the test.
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
            //do nothing
        }

        /**
         * @see org.unitils.UnitilsJUnit4TestClassRunner#run(org.junit.runner.notification.RunNotifier)
         */
        @Override
        public void run(RunNotifier notifier) {
            runMethods(notifier);
        }
    }


}
