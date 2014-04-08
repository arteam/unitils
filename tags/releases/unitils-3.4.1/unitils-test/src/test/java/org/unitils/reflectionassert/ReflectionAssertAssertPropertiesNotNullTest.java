package org.unitils.reflectionassert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.UnitilsException;


/**
 * Test the {@link ReflectionAssert}: method assertAccessablePropertiesNotNullTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ReflectionAssertAssertPropertiesNotNullTest {

    @Test(expected = AssertionError.class)
    public void assertAccessablePropertiesNotNullTest_missingPublicProtected_and_PrivateVariables() {
        TestObject childObject1 = new TestObject("child");
      //  TestObject parentObject = new TestObject("name", 1l, 40, childObject);

        ReflectionAssert.assertAccessablePropertiesNotNull("Accessable properties childobject ar not fully set", childObject1);
    }


    /**
     * 
     */
    @Test
    public void assertAccessablePropertiesNotNull_missingPrivateVariable() {
        TestObject childObject1 = new TestObject("child");
        TestObject parentObject1 = new TestObject("name", 1l, 40, childObject1);

        ReflectionAssert.assertAccessablePropertiesNotNull("Accessable properties parentobject ar not fully set", parentObject1);

    }
    
    @Test(expected = UnitilsException.class)
    public void assertAccessablePropertiesNotNull_exceptionInvoke() {
        TestObject2 obj = new TestObject2(25);
        ReflectionAssert.assertAccessablePropertiesNotNull("Fields are not accessible", obj);
    }

    /**
     * 
     * Only there for testing this class
     * 
     * @author tdr
     * 
     * @since 1.0.5
     * 
     */
    @SuppressWarnings("unused")
    private class TestObject {

        private String sickness;

        public String name;

        protected Long id;

        public int age;

        
        TestObject childObject1;

        /**
         * @param name
         * @param id
         * @param age
         */
       
        public TestObject(String name, Long id, int age) {
            this.name = name;
            this.id = id;
            this.age = age;
        }


        /**
         * @param sickness the sickness to set
         */
        public void setSickness(String sickness) {
            this.sickness = sickness;
        }


        /**
         * @param name
         * @param id
         * @param age
         * @param childObject
         */
        public TestObject(String name, Long id, int age, TestObject childObject) {
            super();
            this.name = name;
            this.id = id;
            this.age = age;
            this.childObject1 = childObject;
        }


        /**
         * @param name
         */
        public TestObject(String name) {
            super();
            this.name = name;
        }


        /**
         * @return the name
         */
        public String getName() {
            return name;
        }


        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }


        /**
         * @return the id
         */
        public Long getId() {
            return id;
        }


        /**
         * @param id the id to set
         */
        public void setId(Long id) {
            this.id = id;
        }


        /**
         * @return the age
         */
        public int getAge() {
            return age;
        }


        /**
         * @param age the age to set
         */
        public void setAge(int age) {
            this.age = age;
        }


        /**
         * @return the childObject
         */
        public TestObject getChildObject() {
            return childObject1;
        }


        /**
         * @param childObject the childObject to set
         */
        public void setChildObject(TestObject childObject) {
            this.childObject1 = childObject;
        }


    }
    
    private class TestObject2 {
        private int age;
        
        public TestObject2(int age) {
            this.age = age;
        }
        
        
        /**
         * @param age the age to set
         */
        private void setAge(int age) {
            this.age = age;
        }
        
        
        /**
         * @return the age
         */
        private int getAge() {
            return age;
        }
    }

}
