package org.unitils.dbmaintainer.locator;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.impl.DefaultScriptSource;


/**
 * Locate db scripts.
 * 
 * @author tdr
 * 
 * @since 1.0.2
 * 
 */
public class ClassPathScriptLocator extends ClassPathResourceLocator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ClassPathScriptLocator.class);

    protected List<Script> scriptList;

    protected String path;

    protected List<String> scriptExtensions;
    
    protected String schema;

    /**
     * @param scriptList1
     * @param path1
     * @param resourcePickingStrategie
     * @param scriptExtensions1
     */
    public void loadScripts(List<Script> scriptList1, String path1, ResourcePickingStrategie resourcePickingStrategie, List<String> scriptExtensions1, String schema, boolean defaultDatabase, Properties configuration) {
        this.path = path1;
        this.scriptList = scriptList1;
        this.scriptExtensions = scriptExtensions1;
        this.schema = schema;
        List<URL> matchedResources = loadResources(path1, false);
        List<URL> resourcesF = resourcePickingStrategie.filter(matchedResources, path1);

        try {
            addToScriptList(resourcesF, defaultDatabase, configuration);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void addToScriptList(List<URL> resourcesF, boolean defaultDatabase, Properties configuration) throws IOException {

        for (URL url : resourcesF) {
            if (isScriptFile(url.toString())) {
                String scriptName = url.toString().substring(url.toString().lastIndexOf(path) + path.length());
                DefaultScriptSource defaultScriptSource = new DefaultScriptSource();
                defaultScriptSource.init(configuration);
                if (defaultScriptSource.checkIfScriptContainsCorrectDatabaseName(scriptName, schema, defaultDatabase) && defaultScriptSource.containsOneOfQualifiers(scriptName)) {
                    Script script = new Script(scriptName, Long.valueOf(url.openConnection().getLastModified()), new org.unitils.dbmaintainer.script.ScriptContentHandle.UrlScriptContentHandle(url));

                    logger.debug(" + script added (" + url.toString() + "))");

                    scriptList.add(script);
                }
                
            }
        }

    }

    /**
     * Vefiry's of the <code>location</code> ends with a valid file-extension.
     * 
     * @param location
     * @return
     */
    protected boolean isScriptFile(String location) {
        String name = location;
        for (Iterator<String> i = scriptExtensions.iterator(); i.hasNext();) {
            String fileExtension = i.next();
            if (name.endsWith(fileExtension)) {
                return true;
            }
        }

        return false;
    }
}
