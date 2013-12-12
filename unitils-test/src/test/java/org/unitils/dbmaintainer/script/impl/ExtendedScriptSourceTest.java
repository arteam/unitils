package org.unitils.dbmaintainer.script.impl;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDeleteOnExit;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbmaintainer.script.ExecutedScript;


/**
 * 
 *
 * @author tdr
 *
 * @since 1.0.2
 *
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ExtendedScriptSourceTest {


    /* Tested object */
    ExtendedScriptSource scriptSource;

    String scriptsDirName;

    List<ExecutedScript> alreadyExecutedScripts;

    Date executionDate;

    /**
     * Cleans test directory and copies test files to it. Initializes test objects
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        executionDate = new Date();
        // Create test directories
        scriptsDirName = System.getProperty("java.io.tmpdir") + "ExtendedScriptSourceTest";
        forceDeleteOnExit(new File(scriptsDirName));


       
        // Initialize FileScriptSource object
        Properties configuration = new Properties();

        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, "org/unitils/dbunit/testdbscripts/");
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_EXTENSIONS, "sql");
        configuration.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, "postprocessing");
        configuration.setProperty(DefaultScriptSource.PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, "false");
        configuration.setProperty("database.dialect", "hsqldb");
        scriptSource = new ExtendedScriptSource();
        scriptSource.init(configuration);
    }
    /**
     * @see org.unitils.dbmaintainer.script.impl.ExtendedScriptSourceTest
     */
    @Test
    public void loadAllScriptsTest() {
        Assert.assertEquals(4 , scriptSource.loadAllScripts().size());
        
    }
}
