package org.unitils.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * ResourcebundleCheck: Checks if every key has a value in every locale.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */

public class ResourcebundleCheck {

    /**
     * The ResoucebundleCheck checks if the bundle exists for all the locale's and if keys exists for every bundle.
     * It checks all the keys and it throws an {@link Exception} when one of the exceptions isn't found. An {@link MissingResourceException} is thrown when 
     * a bundle can't be found (this happens only when their is no default resourcebundle).
     * @param bundleName
     * @param locales
     * @throws Exception
     */
    public static void testAllTheKeys(String bundleName, Locale... locales) throws Exception {
        StringBuilder builder = new StringBuilder();
        ResourceBundle[] bundles = new ResourceBundle[locales.length];
        //creates the bundles for each locale
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];

            bundles[i] = ResourceBundle.getBundle(bundleName, locale);
        }
        
        

        //check which bundle is the biggest.
        ResourceBundle biggestBundle = bundles[0];
        for (ResourceBundle bundle : bundles) {
            if (bundle.keySet().size() > biggestBundle.keySet().size()) {
                biggestBundle = bundle;
            }
        }
        Enumeration<String> keys = biggestBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            for (int i = 0; i < bundles.length; i++) {
                //check if the key exists in the bundle.
                if (!bundles[i].containsKey(key)) {

                    builder.append("- Locale ");
                    builder.append(bundles[i].getLocale().toString());
                    builder.append(" doesn't contain '");
                    builder.append(key);
                    builder.append("' \n");
                } else if (bundles[i].getString(key).isEmpty()) {
                    builder.append("Locale ");
                    builder.append(bundles[i].getLocale().toString());
                    builder.append(": '");
                    builder.append(key);
                    builder.append("': is empty.  \n");
                }
            }
        }

        if (!builder.toString().isEmpty()) {
            throw new MissingKeysException(builder.toString());
        }
    }
}
