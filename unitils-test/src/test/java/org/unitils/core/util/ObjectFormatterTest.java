/*
 * Copyright Unitils.org
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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.io.File;

import static org.junit.Assert.assertTrue;


/**
 * Tests the formatting of proxies and mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectFormatterTest extends UnitilsJUnit4 {

    private ObjectFormatter objectFormatter = new ObjectFormatter();


    @Test
    public void formatFile() {
        File file = new File("/somepath/test/file.txt");
        String result = objectFormatter.format(file);
        assertTrue("File</somepath/test/file.txt>".equals(result) || "File<\\somepath\\test\\file.txt>".equals(result));
    }

}