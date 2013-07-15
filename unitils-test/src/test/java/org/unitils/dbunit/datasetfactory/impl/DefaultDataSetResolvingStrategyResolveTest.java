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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.util.FileResolver;
import org.unitils.mock.Mock;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetResolvingStrategyResolveTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetResolvingStrategy defaultDataSetResolver;

    private Mock<FileResolver> fileResolverMock;

    private File file;


    @Before
    public void initialize() throws Exception {
        defaultDataSetResolver = new DefaultDataSetResolvingStrategy(fileResolverMock.getMock());

        file = File.createTempFile("test", "txt");
        fileResolverMock.returns(file.toURI()).resolveFileName("file name", DefaultDataSetResolvingStrategyResolveTest.class);
    }


    @Test
    public void resolve() {
        File result = defaultDataSetResolver.resolve(DefaultDataSetResolvingStrategyResolveTest.class, "file name");
        assertEquals(file, result);
    }
}