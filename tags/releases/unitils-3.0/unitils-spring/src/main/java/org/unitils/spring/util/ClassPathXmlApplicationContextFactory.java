/*
 * Copyright 2008,  Unitils.org
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
 * An {@link ApplicationContextFactory} that creates an instance of
 * the type <code>ClassPathXmlApplicationContext</code>.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ClassPathXmlApplicationContextFactory implements ApplicationContextFactory {


    /**
     * Create an <code>ClassPathXmlApplicationContext</code> for the given locations on which refresh has not
     * yet been called
     *
     * @param locations The configuration file locations, not null
     * @return A context, on which the <code>refresh()</code> method hasn't been called yet
     */
    public ConfigurableApplicationContext createApplicationContext(List<String> locations) {
        return new ClassPathXmlApplicationContext(locations.toArray(new String[locations.size()]), false);
    }
}
