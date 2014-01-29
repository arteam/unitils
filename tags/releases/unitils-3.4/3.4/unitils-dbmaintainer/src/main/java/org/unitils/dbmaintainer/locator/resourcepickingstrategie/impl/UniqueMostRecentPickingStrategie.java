package org.unitils.dbmaintainer.locator.resourcepickingstrategie.impl;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;


/**
 * Implementation of the  @link{ResourcePickingStrategie}.
 * will filter out the duplicates and will hold the most recent one.
 *
 * @author tdr
 *
 * @since 0.1.2
 *
 */
public class UniqueMostRecentPickingStrategie implements ResourcePickingStrategie {

    private static Log logger = LogFactory.getLog(UniqueMostRecentPickingStrategie.class);

    /**
     * @see org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie#filter(java.util.List)
     */
    public List<URL> filter(List<URL> resources, String resourceSearchName) {
        List<URL> filteredResources = new ArrayList<URL>();

        for (URL url : resources) {
            try {
                addMostRecent(filteredResources, url, resourceSearchName);
            } catch (IOException e) {
                logger.error("could not add resource", e);
            }
        }
        return filteredResources;

    }
    /**
     * Will look to add the urlNew in the filteredResource. if duplicate exists (based on resourceSearchName) it will look at
     * last modification date and take the most recent one.
     * 
     * @param filteredResources
     * @param urlNew
     * @param resourceSearchName
     * @throws IOException
     */
    public void addMostRecent(List<URL> filteredResources, URL urlNew, String resourceSearchName) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        
        for (int i = 0; i < filteredResources.size(); i++) {
            URL url = filteredResources.get(i);
            String endingWith = url.toString().substring(url.toString().lastIndexOf(resourceSearchName));
            //    logger.debug("List Resource ending with :" + endingWith);

                if (urlNew.toString().endsWith(endingWith)) {
                    Long inlistresource = url.openConnection().getLastModified();
                    Long newresource = urlNew.openConnection().getLastModified();
                    if (inlistresource < newresource) {
                        logger.debug("Resource replace by more recent after PickingStrategie: '" + url + "'(" + sdf.format(new Date(inlistresource)) + ") replaced_by '" + urlNew + "' (" + sdf.format(new Date(newresource)) + ")");
                        filteredResources.remove(url);
                        filteredResources.add(urlNew);
                    } else {
                        logger.debug("Duplicate Resource found after PickingStrategie but not more recent (not added): '" + url + "'(" + sdf.format(new Date(inlistresource)) + ") is choosen over '" + urlNew + "' (" + sdf.format(new Date(newresource)) + ")");
                    }
                    return;
                }
        }

        filteredResources.add(urlNew);
        logger.debug("Resource added after Picking Strategie " + urlNew + " ");


    }
}
