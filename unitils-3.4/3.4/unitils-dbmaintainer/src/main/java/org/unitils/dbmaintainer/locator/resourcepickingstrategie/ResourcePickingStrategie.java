package org.unitils.dbmaintainer.locator.resourcepickingstrategie;

import java.net.URL;
import java.util.List;


/**
 * Defines a Strategie for picking out resources.
 * 
 *
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 *
 */
public interface ResourcePickingStrategie {

    /**
     * @param list
     * @param searchedPath
     * @return List<URL>
     */
    List<URL> filter(List<URL> list, String searchedPath);
}
