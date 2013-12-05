/*
 * CVS file status:
 * 
 * $Id: ExtendedScriptSource.java,v 1.2 2011-11-03 12:25:00 jeho Exp $
 * 
 * Copyright (c) Smals
 */
package org.unitils.dbmaintainer.script.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.locator.ClassPathScriptLocator;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.impl.UniqueMostRecentPickingStrategie;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.util.PropertyUtils;

/**
 * Implementation of {@link ScriptSource} that reads script files from the classpath.
 * <p/>
 * <p>
 * Wildcards in naming are allowed. When looking through the classpath it will use the {@link PathMatchingResourcePatternResolver} provided
 * by Spring.
 * </p>
 * When finding multiple files that have the same name the system will take te most recent one. Script files should be located in the
 * package configured by {@link #PROPKEY_SCRIPT_LOCATIONS}. Valid script files start with a version number followed by an underscore, and
 * end with the extension configured by {@link #PROPKEY_SCRIPT_EXTENSIONS}.
 * 
 * @author Thomas De Rycke
 * 
 * @since 1.0.2
 * 
 */
public class ExtendedScriptSource extends DefaultScriptSource {

    private static final Log LOGGER = LogFactory.getLog(ExtendedScriptSource.class);

    /** */
    public ExtendedScriptSource() {
    }

    /**
     * 
     * @see org.unitils.dbmaintainer.script.impl.DefaultScriptSource#loadAllScripts()
     */
    protected List<Script> loadAllScripts() {
        List<String> scriptLocations = PropertyUtils.getStringList("dbMaintainer.script.locations", configuration);
        List<String> scriptIgnoredLocations = PropertyUtils.getStringList("dbMaintainer.script.locations.ignore", configuration);

        List<String> ignoredSubLocations = new ArrayList<String>();
        for (String locationIgnorded : scriptIgnoredLocations) {

            for (String scriptLocation : scriptLocations) {
                if (locationIgnorded.startsWith(scriptLocation)) {
                    String sublocation = locationIgnorded.replace(scriptLocation, "");
                    if (sublocation.startsWith("/")) {
                        sublocation = sublocation.substring(1);
                    }
                    ignoredSubLocations.add(sublocation);
                }
            }
        }

        LOGGER.debug("Ignorded sublocations for script search: " + ArrayUtils.toString(ignoredSubLocations.toArray()));


        List<Script> scripts = new ArrayList<Script>();
        String scriptLocation;
        for (Iterator<String> i = scriptLocations.iterator(); i.hasNext(); getScriptsAt(scripts, scriptLocation, "")) {
            scriptLocation = i.next();
        }

        List<Script> scriptsToRemove = new ArrayList<Script>();
        for (Script script : scripts) {
            for (String ignoredSublocation : ignoredSubLocations) {
                if (script.getFileName().startsWith(ignoredSublocation)) {
                    LOGGER.debug("Removing script '" + script.getFileName() + "' because in ignored sub-location " + ignoredSublocation);
                    scriptsToRemove.add(script);
                }
            }
        }
        scripts.removeAll(scriptsToRemove);

        LOGGER.debug("Scripts found: " + scripts.size());
        return scripts;
    }

    /**
     * 
     * @see org.unitils.dbmaintainer.script.impl.DefaultScriptSource#getScriptsAt(java.util.List, java.lang.String, java.lang.String)
     */
    protected void getScriptsAt(List<Script> scripts, String scriptRoot, String relativeLocation) {

        String location = scriptRoot;

        LOGGER.debug("Script location: " + location);

        ClassPathScriptLocator classPathScriptLocator = new ClassPathScriptLocator();
        classPathScriptLocator.loadScripts(scripts, scriptRoot, getResourcePickingStrategie(), getScriptExtensions());


    }


    /**
     * use unitil propertie instead of hardcoding
     * 
     * @return
     */
    protected ResourcePickingStrategie getResourcePickingStrategie() {
        return new UniqueMostRecentPickingStrategie();
    }


}