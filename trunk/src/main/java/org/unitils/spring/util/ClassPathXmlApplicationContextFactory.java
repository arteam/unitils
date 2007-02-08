/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.spring.util;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * {@link ApplicationContextFactory} that creates an instance of the type <code>ClassPathXmlApplicationContext</code>.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ClassPathXmlApplicationContextFactory implements ApplicationContextFactory {


    // todo javadoc
    public ConfigurableApplicationContext createApplicationContext(List<String> locations) {
        // create application context
        return new ClassPathXmlApplicationContext(locations.toArray(new String[locations.size()]), false);
    }
}
