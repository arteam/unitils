package org.unitils.core;

import org.apache.commons.configuration.Configuration;

/**
 * todo javadoc
 */
public interface UnitilsModule {

    public void init(Configuration configuration);

    public TestListener createTestListener();

}
