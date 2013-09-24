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
package org.unitils.io.conversion;

import java.io.IOException;
import java.io.InputStream;

/**
 * A conversion strategy converts the given input stream to the object specified (T). It will consider the given
 * encoding when doing the conversion.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public interface ConversionStrategy<T> {

    /**
     * Converts the content of the given stream into the target type.
     * The stream will not be closed during this method.
     *
     * @param inputStream The stream with the content, not null
     * @param encoding    The encoding to use when reading the stream, not null
     * @return The converted instance of the target type, not null
     */
    T convertContent(InputStream inputStream, String encoding) throws IOException;


    /**
     * @return The default extension to use when no extension is provided, not null
     */
    String getDefaultFileExtension();

    /**
     * @return The target type for the conversion, not null
     */
    Class<T> getTargetType();

}
