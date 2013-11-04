/*
 * CVS file status:
 * 
 * $Id: ResourcePickingStrategie.java,v 1.1 2010-11-22 12:10:10 jeho Exp $
 * 
 * Copyright (c) Smals
 */
package org.unitils.dbunit.resourcepickingstrategie;

import java.net.URL;
import java.util.List;


/**
 * Defines a Strategie for picking out resources.
 * 
 *
 * @author tdr
 *
 * @since 1.0.2
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
