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

package org.unitils.io;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.io.annotation.FileContent;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOModuleIntegrationTest extends UnitilsJUnit4 {

    @FileContent
    protected String fileContent;

    @FileContent
    protected Properties propertiesContent;

    @Test
    public void filledUpValuesTest() {
        assertEquals("testFile", fileContent);
        assertEquals("testFile", propertiesContent.getProperty("testFile"));
    }

}
