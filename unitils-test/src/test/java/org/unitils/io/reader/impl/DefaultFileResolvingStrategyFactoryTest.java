/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.io.reader.impl;

import org.junit.Test;
import org.unitils.io.reader.FileResolvingStrategyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class DefaultFileResolvingStrategyFactoryTest {


    //This test is failing for now --> as intended.
    @Test
    public void defaultSetup() {
        FileResolvingStrategyFactory defaultFileResolvingStrategyFactory = new FileResolvingStrategyFactory("true", "prefix");
        DefaultFileResolvingStrategy fileResolvingStrategy = (DefaultFileResolvingStrategy) defaultFileResolvingStrategyFactory.create();


        assertEquals("prefix", fileResolvingStrategy.fileResolver.getPathPrefix());
        assertTrue(fileResolvingStrategy.fileResolver.isPrefixWithPackageName());
    }

    @Test
    public void emptyPathPrefix() {
        FileResolvingStrategyFactory defaultFileResolvingStrategyFactory = new FileResolvingStrategyFactory("true", "");

        DefaultFileResolvingStrategy fileResolvingStrategy = (DefaultFileResolvingStrategy) defaultFileResolvingStrategyFactory.create();
        assertEquals("", fileResolvingStrategy.fileResolver.getPathPrefix());
    }

}
