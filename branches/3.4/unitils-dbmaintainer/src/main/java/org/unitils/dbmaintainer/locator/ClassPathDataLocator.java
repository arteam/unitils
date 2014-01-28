package org.unitils.dbmaintainer.locator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;


/**
 * Refactor this class to work with a strategy class for choosing resources.
 * 
 * @author tdr
 * 
 * @since 1.0.2
 * 
 */
public class ClassPathDataLocator extends ClassPathResourceLocator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ClassPathDataLocator.class);

    
    /**
     * The resources are loaded and than does the {@link ResourcePickingStrategie} the first filtering.<br/>
     * Possibilities:
     * <ul>
     *  <li>There are still more than one resources: the first resource is returned</li>
     *  <li>There is only one resource: this resource is returned.</li>
     *  <li>There aren't any resources left: null will be returned.</li>
     * </ul>
     * @param resourceName
     * @param resourcePickingStrategie
     * @return {@link InputStream}
     */
    public InputStream getDataResource(String resourceName, ResourcePickingStrategie resourcePickingStrategie) {
        List<URL> matchedResources = loadResources(resourceName, true);
      //  List<URL> resourcesF = chooseMostRecent(matchedResources, resourceName);
        List<URL> resourcesF = resourcePickingStrategie.filter(matchedResources, resourceName);
       
        try {
            if (resourcesF.size() > 1) {
                logger.warn("Multiple resources found for '" + resourceName + "'. Ambigues resourceName. Will choose first occurence");

                return resourcesF.get(0).openStream();

            } else if (resourcesF.size() == 1) {
                logger.info("One resources found for '" + resourceName + "'. ");
                return resourcesF.get(0).openStream();
            }
        } catch (IOException e) {
            logger.error("could open stream", e);
        }
        return null;
    }

}
