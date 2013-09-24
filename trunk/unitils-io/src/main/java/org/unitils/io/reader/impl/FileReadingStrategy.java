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
package org.unitils.io.reader.impl;

import org.unitils.io.reader.FileResolvingStrategy;
import org.unitils.io.reader.ReadingStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileReadingStrategy implements ReadingStrategy {

    protected FileResolvingStrategy fileResolvingStrategy;


    public FileReadingStrategy(FileResolvingStrategy fileResolvingStrategy) {
        this.fileResolvingStrategy = fileResolvingStrategy;
    }


    public InputStream getDefaultInputStream(String extension, Class<?> testClass) throws IOException {
        URI fileURI = fileResolvingStrategy.resolveDefaultFileName(extension, testClass);
        return fileURI.toURL().openStream();
    }

    public InputStream getInputStream(String fileName, Class<?> testClass) throws IOException {
        URI fileURI = fileResolvingStrategy.resolveFileName(fileName, testClass);
        return fileURI.toURL().openStream();
    }
}