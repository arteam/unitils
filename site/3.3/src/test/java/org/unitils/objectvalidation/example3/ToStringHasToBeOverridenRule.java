/*
 * Copyright (c) Smals
 */
package org.unitils.objectvalidation.example3;

import org.unitils.objectvalidation.Rule;


/**
 * Example 3: writing a rule.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
//START SNIPPET: objectvalidationExample3
public class ToStringHasToBeOverridenRule implements Rule {

    /**
     * @see org.unitils.objectvalidation.Rule#validate(java.lang.Class)
     */
    @Override
    public void validate(Class<?> classToValidate) {
        // Do something

    }

}
//END SNIPPET: objectvalidationExample3