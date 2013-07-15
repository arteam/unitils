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

package org.unitils.io.reader;

import org.unitils.core.Factory;
import org.unitils.io.reader.impl.FileReadingStrategy;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class ReadingStrategyFactory implements Factory<ReadingStrategy> {

    protected FileResolvingStrategy fileResolvingStrategy;


    public ReadingStrategyFactory(FileResolvingStrategy fileResolvingStrategy) {
        this.fileResolvingStrategy = fileResolvingStrategy;
    }


    public ReadingStrategy create() {
        return new FileReadingStrategy(fileResolvingStrategy);
    }
}
