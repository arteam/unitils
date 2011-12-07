/*
 *
 *  Copyright 2010,  Unitils.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.unitils.IO.example3;

import org.unitils.UnitilsJUnit4;
import org.unitils.io.annotation.TemporaryFile;
import org.unitils.io.annotation.TemporaryFolder;

import java.io.File;

// START SNIPPET: temporaryFile
public class MyFileTest extends UnitilsJUnit4 {
    @TemporaryFile
    private File file;

    @TemporaryFolder
    private File folder;
} 
// END SNIPPET: temporaryFile