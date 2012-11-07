/*
 * Copyright 2012,  Unitils.org
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

package org.unitils.io.conversion.impl;

import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.util.ReaderInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This conversion strategy will try to convert the input stream into a Properties. The default file extension for this
 * conversion strategy is properties. So when not overriding the default file when using the @FileContent the file should.
 * end with '.properties' .
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class PropertiesConversionStrategy implements ConversionStrategy<Properties> {

    // todo td close input stream


    public Properties convertContent(InputStream inputStream, String encoding) throws IOException {
        InputStream readerInputStream = new ReaderInputStream(new InputStreamReader(inputStream, encoding));

        Properties result = new Properties();
        result.load(readerInputStream);
        return result;
    }


    public String getDefaultFileExtension() {
        return "properties";
    }

    public Class<Properties> getTargetType() {
        return Properties.class;
    }

}
