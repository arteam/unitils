package org.unitils.parameterized;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.UnitilsParameterized;

/**
 * Test {@link UnitilsParameterized}
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsParameterized.class)
public class UnitilsParametersNullParametersStveParametersTest {
    private static final Logger LOGGER = Logger.getLogger(UnitilsParametersNullParametersStveParametersTest.class);
    
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }, { null } };
        return Arrays.asList(data);
    }
    
    private Integer number;
    
    public UnitilsParametersNullParametersStveParametersTest(Integer number) {
        this.number = number;
    }

    @Test
    public void test() {
        LOGGER.debug(number);
    }

}
