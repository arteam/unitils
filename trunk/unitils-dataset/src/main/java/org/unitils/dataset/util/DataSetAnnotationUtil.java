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
package org.unitils.dataset.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dataset.DataSetModule;
import org.unitils.dataset.annotation.DataSet;
import org.unitils.dataset.annotation.ExpectedDataSet;
import org.unitils.dataset.factory.DataSetFactory;
import org.unitils.dataset.loader.DataSetLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotation;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getClassValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.getClassWithName;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAnnotationUtil {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetAnnotationUtil.class);

    /* Map holding the default configuration of the dbunit module annotations */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;


    public DataSetAnnotationUtil(Properties configuration) {
        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DataSetModule.class, configuration, DataSet.class, ExpectedDataSet.class);
    }


    /**
     * @param testClass     The test class, not null
     * @param testMethod    The test method, not null
     * @param fileExtension The extension of the data set files (should not start with a '.'), not null
     * @return The file names that were specified by the DataSet annotation, null if not found
     */
    public List<String> getDataSetFileNames(Class<?> testClass, Method testMethod, String fileExtension) {
        DataSet dataSetAnnotation = getMethodOrClassLevelAnnotation(DataSet.class, testMethod, testClass);
        if (dataSetAnnotation == null) {
            // No @DataSet annotation found
            return null;
        }

        // Get the dataset file name
        String[] dataSetFileNames = dataSetAnnotation.value();
        if (dataSetFileNames.length == 0) {
            // empty means, use default file name, which is the name of the class + extension
            dataSetFileNames = new String[]{getDefaultDataSetFileName(testClass, fileExtension)};
        }
        return asList(dataSetFileNames);
    }

    /**
     * Gets the data set factory class that was specified using the factory parameter on the DataSet annotation.
     * If no factory was specified, the default class found in the Unitils configuration will be returned.
     *
     * @param testClass  The test class, not null
     * @param testMethod The test method, not null
     * @return The DataSetFactory class, not null
     */
    @SuppressWarnings("unchecked")
    public Class<? extends DataSetFactory> getDataSetFactoryClass(Class<?> testClass, Method testMethod) {
        Class<? extends DataSetFactory> dataSetFactoryClass = getMethodOrClassLevelAnnotationProperty(DataSet.class, "factory", DataSetFactory.class, testMethod, testClass);
        return (Class<? extends DataSetFactory>) getClassValueReplaceDefault(DataSet.class, "factory", dataSetFactoryClass, defaultAnnotationPropertyValues, DataSetFactory.class);
    }

    public Class<? extends DataSetFactory> getDefaultDataSetFactoryClass() {
        String className = defaultAnnotationPropertyValues.get(DataSet.class).get("factory");
        return getClassWithName(className);
    }

    public List<String> getVariables(Class<?> testClass, Method testMethod) {
        String[] variables = getMethodOrClassLevelAnnotationProperty(DataSet.class, "variables", new String[0], testMethod, testClass);
        return asList(variables);
    }

    /**
     * Gets the data set loader class that was specified using the loadStrategy parameter on the DataSet annotation.
     * If no loader was specified, the default class found in the Unitils configuration will be returned.
     *
     * @param testClass  The test class, not null
     * @param testMethod The method, not null
     * @return The DataSetLoader class, not null
     */
    @SuppressWarnings({"unchecked"})
    public Class<? extends DataSetLoader> getDataSetLoaderClass(Class<?> testClass, Method testMethod) {
        Class<? extends DataSetLoader> dataSetOperationClass = getMethodOrClassLevelAnnotationProperty(DataSet.class, "loader", DataSetLoader.class, testMethod, testClass);
        return (Class<? extends DataSetLoader>) getClassValueReplaceDefault(DataSet.class, "loader", dataSetOperationClass, defaultAnnotationPropertyValues, DataSetLoader.class);
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files
     * @return The default filename, not null
     */
    protected String getDefaultDataSetFileName(Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + '.' + extension;
    }

}