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
package org.unitils.dbunit.datasetfactory.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.unitils.dbunit.datasetfactory.impl.DefaultDataSetResolver.PROPKEY_DATA_SET_PATH_PREFIX;
import static org.unitils.dbunit.datasetfactory.impl.DefaultDataSetResolver.PROPKEY_PREFIX_WITH_PACKAGE_NAME;

/**
 * Tests the default data set resolver.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetResolverTest extends UnitilsJUnit4 {

    /* Tested instance  */
    private DefaultDataSetResolver defaultDataSetResolver;

    /* The unitils properties */
    private Properties configuration;


    /**
     * Initialize test fixture
     */
    @Before
    public void setUp() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolver();
        configuration = new ConfigurationLoader().loadConfiguration();
    }


    /**
     * Test resolving a data set.
     * This will resolve to org/unitils/dbunit/datasetfactory/impl/DefaultDataSetResolverTest.xml
     */
    @Test
    public void testResolve() throws Exception {
        defaultDataSetResolver.init(configuration);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverTest.class, "DefaultDataSetResolverTest.xml");
        assertNotNull(result);
    }


    /**
     * Test resolving a data set without package prefix (starts with a slash).
     * This will resolve to org/unitils/DefaultDataSetResolverTest-otherPackage.xml
     */
    @Test
    public void testResolve_noPackagePrefix() throws Exception {
        defaultDataSetResolver.init(configuration);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverTest.class, "/org/unitils/DefaultDataSetResolverTest-otherPackage.xml");
        assertNotNull(result);
    }


    /**
     * Test resolving a data with package prefixing disabled.
     * This will resolve to org/unitils/DefaultDataSetResolverTest-otherPackage.xml
     */
    @Test
    public void testResolve_packagePrefixDisabled() throws Exception {
        configuration.put(PROPKEY_PREFIX_WITH_PACKAGE_NAME, "false");
        defaultDataSetResolver.init(configuration);

        // no slash --> a prefix would have been used
        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverTest.class, "org/unitils/DefaultDataSetResolverTest-otherPackage.xml");
        assertNotNull(result);
    }


    /**
     * Test resolving a data with a dataset prefix configured.
     * This will resolve to org/unitils/dbunit/datasetfactory/impl/DefaultDataSetResolverTest.xml
     */
    @Test
    public void testResolve_pathPrefix() throws Exception {
        configuration.put(PROPKEY_DATA_SET_PATH_PREFIX, "org/unitils");
        defaultDataSetResolver.init(configuration);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverTest.class, "/DefaultDataSetResolverTest-otherPackage.xml");
        assertNotNull(result);
    }


    /**
     * Test resolving a data with an absolute dataset prefix configured (starting with a slash).
     * The class should be loaded directly from the file system (not classpath).
     */
    @Test
    public void testResolve_absolutePathPrefix() throws Exception {
        String directory = new File(DefaultDataSetResolver.class.getClassLoader().getResource("org/unitils/DefaultDataSetResolverTest-otherPackage.xml").toURI()).getParent();
        configuration.put(PROPKEY_DATA_SET_PATH_PREFIX, "/" + directory);
        defaultDataSetResolver.init(configuration);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverTest.class, "/DefaultDataSetResolverTest-otherPackage.xml");
        assertNotNull(result);
    }

}