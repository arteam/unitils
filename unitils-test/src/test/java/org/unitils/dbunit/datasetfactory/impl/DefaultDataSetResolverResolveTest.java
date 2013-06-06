/*
 * Copyright 2013,  Unitils.org
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

import org.junit.Test;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetResolverResolveTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetResolver defaultDataSetResolver;


    @Test
    public void packagePrefix() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolver(true, null);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverResolveTest.class, "DefaultDataSetResolverResolveTest.xml");
        assertEquals("DefaultDataSetResolverResolveTest.xml", result.getName());
        assertTrue(result.exists());
    }

    @Test
    public void noPackagePrefix() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolver(false, null);

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverResolveTest.class, "/datasets/DefaultDataSetResolverResolveTest2.xml");
        assertEquals("DefaultDataSetResolverResolveTest2.xml", result.getName());
        assertTrue(result.exists());
    }

    @Test
    public void pathPrefix() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolver(false, "datasets");

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverResolveTest.class, "DefaultDataSetResolverResolveTest2.xml");
        assertEquals("DefaultDataSetResolverResolveTest2.xml", result.getName());
        assertTrue(result.exists());
    }

    @Test
    public void pathPrefixAndPackagePrefix() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolver(true, "datasets");

        File result = defaultDataSetResolver.resolve(DefaultDataSetResolverResolveTest.class, "DefaultDataSetResolverResolveTest3.xml");
        assertEquals("DefaultDataSetResolverResolveTest3.xml", result.getName());
        assertTrue(result.exists());
    }
}