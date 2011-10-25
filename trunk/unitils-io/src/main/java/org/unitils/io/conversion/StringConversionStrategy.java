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

package org.unitils.io.conversion;

import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class StringConversionStrategy implements ConversionStrategy<String> {

    public String readContent(InputStream inputStream, String encoding) throws IOException {
        StringWriter writer = new StringWriter();
        InputStreamReader in = new InputStreamReader(inputStream, encoding);

        IOUtils.copy(in, writer);
        return writer.toString();
    }

    public String getDefaultPostFix() {
        return "txt";
    }

    public Class<String> getDefaultEndClass() {
        return String.class;
    }

}
