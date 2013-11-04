/*
 * CVS file status:
 * 
 * $Id: ClassPathResourceLocator.java,v 1.1 2010-11-22 12:10:10 jeho Exp $
 * 
 * Copyright (c) Smals
 */
package org.unitils.dbunit.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.unitils.core.UnitilsException;


/**
 * Abstract class to locate resources on the classpath.
 * Will also look in jars that are in the classpath.
 * 
 * @author tdr
 * 
 * @since 1.0.2
 * 
 */
public abstract class ClassPathResourceLocator {
    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ClassPathResourceLocator.class);


    protected List<URL> resourceList;

    /**
     * Load resources from the classpath.
     * if <code>isConcreteResource</code> set to true
     * we search for a concrete resource.
     * then no deeper directory are attempted to search.
     * 
     * @param path
     * @param isConcreteResource
     * @return List<URL>
     */
    public List<URL> loadResources(String path, Boolean isConcreteResource) {
        resourceList = new ArrayList<URL>();

        try {
            // will also check in external referenced jars.
            Enumeration<URL> resources = getClass().getClassLoader().getResources(path);

            while (resources.hasMoreElements()) {

                URL url = resources.nextElement();

                resourceList.add(url);
                logger.debug(" Resource '" + url.toString() + "' added to resourcelist ");

                if (!isConcreteResource) {
                    List<URL> subResources = searchResources(url);
                    resourceList.addAll(subResources);
                }

            }
        } catch (IOException e) {
            throw new UnitilsException("Unable to scan for resources in path " + path + "", e);
        }

        return resourceList;
    }


    /**
     * Will use the Spring {@link PathMatchingResourcePatternResolver} to find a resource that corresponds to the <code>url</code>.
     * 
     * @param url
     * @return List<URL>
     * @throws IOException
     */
    protected List<URL> searchResources(URL url) throws IOException {
        PathMatchingResourcePatternResolver p = new PathMatchingResourcePatternResolver();
        Resource[] scriptResources = p.getResources(url.toString() + "**");
        List<URL> listScriptResources = new ArrayList<URL>();

        for (int i = 0; i < scriptResources.length; i++) {
            URL urlResource = scriptResources[i].getURL();
            listScriptResources.add(urlResource);
            logger.debug(" Resource '" + urlResource.toString() + "' added to resourcelist ");

        }

        return listScriptResources;
    }
}
