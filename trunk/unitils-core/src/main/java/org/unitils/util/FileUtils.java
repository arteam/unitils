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
package org.unitils.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.write;


/**
 * @author Tim Ducheyne
 */
public class FileUtils {

    /**
     * Writes the given string to the given file
     *
     * @param file   the file to write, not null
     * @param string the string, not null
     */
    public static void writeStringToFile(File file, String string) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            write(string, out, null);
        } finally {
            closeQuietly(out);
        }
    }
}
