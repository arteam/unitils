/*
 * Copyright (c) Smals
 */
package org.unitils.springbatch.example1;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.batch.BatchTest;
import org.unitils.spring.batch.annotations.BatchTestEnvironment;
import org.unitils.spring.batch.annotations.BatchTestPlaceHolder;


/**
 * Example 1.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
@Ignore
//START SNIPPET: springBatchExample1
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SpringBatchTest1 {

    
    @BatchTestPlaceHolder
    private BatchTest batchTest;

    @Test
    @BatchTestEnvironment(contextFile="spring/batch/jobs/job-hello-world.xml", job="helloWorldJob")
    public void test() throws Exception {
        batchTest.launchJob();
    }

}
//END SNIPPET: springBatchExample1