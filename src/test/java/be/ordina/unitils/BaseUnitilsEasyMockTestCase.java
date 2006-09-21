package be.ordina.unitils;

import be.ordina.unitils.testing.mock.BaseEasyMockJUnit3TestCase;

/**
 * Base class for all unit tests that use mock objects for the Unitils project
 */
public class BaseUnitilsEasyMockTestCase extends BaseEasyMockJUnit3TestCase {

    private static final String UNITTEST_PROPERTIES_FILE = "unitils-tests.properties";

    protected final String getPropertiesFileName() {
            return UNITTEST_PROPERTIES_FILE;
    }


}
