/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 */

package org.unitils.io.example8;

import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.io.IOUnitils;

import java.util.Properties;

// START SNIPPET: fileContent
public class MyFileTest {

    @Before
    public void initialize() {
        String content1 = IOUnitils.readFileContent(String.class, this);
        String content2 = IOUnitils.readFileContent("myFile.csv", String.class, this);

        Properties properties1 = IOUnitils.readFileContent(Properties.class, this);
        Properties properties2 = IOUnitils.readFileContent("myProperties.properties", Properties.class, this);
    }

}
// END SNIPPET: fileContent