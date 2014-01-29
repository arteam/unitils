package org.unitils.parameterized;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * JustATestClass.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@Ignore
public class JustATestClass {
	
	public JustATestClass() {
		//do nothing
	}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        //empty
    }

    @Test
    public static void test1() {
        //test method
    }
    
    @Test
    protected void test2() {
        //test method
    }
    
    @Test
    public String test3() {
        return "";
    }
    
    @Test
    public void test4(String param1) {
        //test method
    }
    
    
    
}
