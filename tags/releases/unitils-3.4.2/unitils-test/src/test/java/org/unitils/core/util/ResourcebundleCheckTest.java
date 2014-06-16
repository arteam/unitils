package org.unitils.core.util;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Ignore;
import org.junit.Test;
import org.unitils.util.MissingKeysException;
import org.unitils.util.ResourcebundleCheck;


/**
 * Resourcebundlechecktest.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class ResourcebundleCheckTest {
    @Test
    public void testSuccess() throws Exception {
        ResourcebundleCheck.testAllTheKeys("resourcebundle/comments", new Locale("fr"), new Locale("nl"), new Locale("de"));

    }
    /**
     * The french bundle contains more keys than the others.
     * invoice_declaration_yes, invoice_declaration_no =
     * @throws Exception
     */
    @Test(expected= MissingKeysException.class)
    public void testBad() throws Exception {
        ResourcebundleCheck.testAllTheKeys("resourcebundle/invoice", new Locale("nl"), new Locale("fr"), new Locale("de"));
    }
    
    @Ignore
    @Test(expected = MissingResourceException.class)
    public void testBadBundleDoesNotExist() throws Exception {
        ResourcebundleCheck.testAllTheKeys("resourcebundle/invoice", new Locale("fr"), new Locale("nl"), new Locale("de"), new Locale("sq"));
    }
}
