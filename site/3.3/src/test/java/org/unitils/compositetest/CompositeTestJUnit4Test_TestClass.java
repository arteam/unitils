/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.compositetest;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import org.junit.Test;
import org.unitils.UnitilsJUnit4TestBase;
import org.unitils.compositetest.annotation.CompositeTestHandler;
import org.unitils.compositetest.annotation.TestPart;

/**
 * JUnit 4 test class containing a multipart setup.
 *
 * @author Jef Verelst
 */
//START SNIPPET: compositetesttest
public class CompositeTestJUnit4Test_TestClass extends UnitilsJUnit4TestBase {


    @CompositeTestHandler
    private CompositeTestDriver mpTestDriver;

	
    @Test
    public void testMain() {
        System.out.println("JU4 MAIN");
        registerTestInvocation(TEST_METHOD, this.getClass(), "main");
    }
        
        
    @Test
    @TestPart(name="part2")    
    public void testPart2() {
        System.out.println("JU4 PART2");
        registerTestInvocation(TEST_METHOD, this.getClass(), "part2");
    }
    
    @Test
    @TestPart(name="part3", executeAsSingleTest=true)    
    public void testPart3() {
        System.out.println("JU4 PART3");
        registerTestInvocation(TEST_METHOD, this.getClass(), "part3");
    }
    
    @Test
    public void multi() {
        System.out.println("JU4 MULTI");        
        this.mpTestDriver.launchTestPart("testPart1"); // via method name
        this.mpTestDriver.launchTestPart("part2");        
        this.mpTestDriver.launchTestPart("part3");
        registerTestInvocation(TEST_METHOD, this.getClass(), "multi"); // has to be the last, otherwise the check on executed method freaks out.
    }
    
    @Test
    @TestPart
    // behind the multi() method, order is not important
    public void testPart1() {
        System.out.println("JU4 PART1");
        registerTestInvocation(TEST_METHOD, this.getClass(), "part1");
    }

}
//END SNIPPET: compositetesttest
