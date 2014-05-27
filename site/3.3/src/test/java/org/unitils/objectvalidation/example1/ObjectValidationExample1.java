/*
 * Copyright (c) Smals
 */
package org.unitils.objectvalidation.example1;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.objectvalidation.ObjectValidationRules;
import org.unitils.objectvalidation.ObjectValidator;


/**
 * Example1.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
@Ignore
//START SNIPPET: objectvalidationExample1
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ObjectValidationExample1 {

    @ObjectValidationRules
    private ObjectValidator objectValidator;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        objectValidator.
            classToValidate(ValidBean.class).checkingAllPossibilities().withAllFields().
            classToValidate(ValidBeanWithByteArray.class).checkingAllPossibilities().withAllFields().
            classToValidate(ValidBeanOnlyId.class).checkingAllPossibilities().withFieldNames("id").
            validate();
    }

}
//END SNIPPET: objectvalidationExample1