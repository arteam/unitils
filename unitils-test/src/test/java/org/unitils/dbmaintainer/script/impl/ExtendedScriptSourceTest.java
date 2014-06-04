package org.unitils.dbmaintainer.script.impl;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDeleteOnExit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;


/**
 * 
 *
 * @author tdr
 *
 * @since 1.0.2
 *
 */
@Ignore
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ExtendedScriptSourceTest {


    /* Tested object */
    ResourceScriptSource scriptSource;

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
        scriptSource = new ResourceScriptSource();
        scriptSource.init(configuration);
    }
    /**
     * @see org.unitils.dbmaintainer.script.impl.ExtendedScriptSourceTest
     */
    @Test
    public void loadAllScriptsTest() {
        Assert.assertEquals(7 , scriptSource.loadAllScripts("public", "users", true).size());
        
    }
    
    @Test
    public void testGetScriptsAt() throws Exception {
        List<Script> actual = new ArrayList<Script>();
        
        scriptSource.getScriptsAt(actual, "org/unitils/dbunit/testdbscripts/", "", "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }
        
        Assert.assertEquals(7, actual.size());
        ReflectionAssert.assertReflectionEquals(Arrays.asList("file2.sql", "@users_addusers.sql", "01_@users_addusers.sql", "testsubpackage/004_Initial_TESTcreate.sql", "001_Initial_TESTcreate.sql", "002_Initial_TESTcreate.sql", "003_Initial_TESTcreate.sql"), actualNames, ReflectionComparatorMode.LENIENT_ORDER);
    }
}
