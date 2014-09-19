/*
 * Copyright (c) Smals
 */
package org.unitils.dbmaintainer.script.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;


/**
 * test qualifiers and multi user support in {@link ResourceScriptSource}.
 * 
 * @author wiw
 * 
 * @since 
 * 
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class ResourceScriptSourceQualifiersMultiSupportTest {
    private static final String LOCATION = "org/unitils/dbunit/testdbscripts";
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(new File("target/test-classes/" + LOCATION));
    @TestedObject
    private ResourceScriptSource scriptSource;
    private Properties configuration;
    
    private static final String PROPKEY_EXTENTION = "dbMaintainer.script.fileExtensions";

    
    @Before
    public void setUp() {
        configuration = new Properties();
        configuration.put(PROPKEY_EXTENTION, "sql");
    }
    
    @Test
    public void testCheckIfFileMustBeAddedToScriptList() throws Exception {
        String schema1 = "USERS";
        String schema2 = "pEoplE";

        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("test123.sql", "public", false));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("test123.sql", "public", true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("@users_addusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("@usersaddusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("1@users_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("1_@users_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@users_addusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("01@users_addusers.sql", schema1, true));

        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("01@users_addpeople.sql", schema2, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("1@people_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@people_addusers.sql", schema2, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@people_addUsers.sql", schema2, true));
    }

    /**
     * test {@link DefaultScriptSource#checkIfThereAreNoQualifiers(String)}
     * @throws Exception
     */
    @Test
    public void testCheckIfThereAreNoQualifiers() throws Exception {
        Assert.assertTrue(scriptSource.checkIfThereAreNoQualifiers("01_products.sql"));
        Assert.assertFalse(scriptSource.checkIfThereAreNoQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.checkIfThereAreNoQualifiers("#refdata_#postgres_products.sql"));
    }

    @Test
    public void testContainsOneOfQualifiers_withoutIncludes() throws Exception {
        scriptSource = new ResourceScriptSource();
        Properties configuration = new Properties();
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2, exclude3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2, exclude3");

        scriptSource.init(configuration);

        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_#include2_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("#include1_#include2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#include1_#exclude2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#exclude1_products.sql"));
    }

    @Test
    public void testContainsOneOfQualifiers_withIncludes() throws Exception {
        scriptSource = new ResourceScriptSource();
        Properties configuration = new Properties();
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2, exclude3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2, exclude3");

        scriptSource.init(configuration);

        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_#include2_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("#include1_#include2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#include1_#exclude2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#exclude1_products.sql"));
    }

    @Test
    public void testGetScriptsAt_multiUserSupport() throws Exception {
        File parentFile = tempFolder.newFolder("test1");
        tempFolder.newFile("test1/file1.txt");
        tempFolder.newFile("test1/file2.sql");
        tempFolder.newFile("test1/@users_addusers.sql");
        tempFolder.newFile("test1/01_@users_addusers.sql");
        tempFolder.newFile("test1/1@people_addusers.sql");

        String path = getPath(parentFile);
        List<Script> actual = new ArrayList<Script>();
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, LOCATION);
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_EXTENSIONS, "sql");
        configuration.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, "postprocessing");
        configuration.setProperty(DefaultScriptSource.PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, "false");
        
        scriptSource.init(configuration);
        scriptSource.getScriptsAt(actual, path, "test1", "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }

        Assert.assertEquals(3, actual.size());
        ReflectionAssert.assertReflectionEquals(Arrays.asList("file2.sql", "@users_addusers.sql", "01_@users_addusers.sql"), actualNames, ReflectionComparatorMode.LENIENT_ORDER);

    }

    @Test
    public void testGetScriptsAt_qualifiers() throws Exception {
        String nameFolder = "getscriptsat_qualifiers";
        File parentFile = tempFolder.newFolder(nameFolder);

        tempFolder.newFile(nameFolder + "/01_#include1_products.sql");
        tempFolder.newFile(nameFolder + "/#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#refdata_#postgres_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#exclude2_products.sql");

        String path = getPath(parentFile);
        scriptSource = new ResourceScriptSource();
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2");
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, path);
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_EXTENSIONS, "sql");
        configuration.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, "postprocessing");
        configuration.setProperty(DefaultScriptSource.PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, "false");
        configuration.setProperty("database.dialect", "hsqldb");
        
        scriptSource.init(configuration);

        
        List<Script> actual = new ArrayList<Script>();

        scriptSource.getScriptsAt(actual, path, nameFolder, "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }

        Assert.assertEquals(3, actualNames.size());

        ReflectionAssert.assertLenientEquals(Arrays.asList("01_#include1_products.sql", "#include1_#include2_products.sql", "01_#include1_#include2_products.sql"), actualNames);
    }
    
    @Test
    public void testGetScriptsAt_qualifiersAndMultiUserSupport_defaultDatabase() throws Exception {
        String nameFolder = "getscriptsat";
        File parentFile = tempFolder.newFolder(nameFolder);
        
        
        tempFolder.newFile(nameFolder + "/01_#include1_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include2_@users_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include2_@people_products.sql");
        
        tempFolder.newFile(nameFolder + "/#include1_@people_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/@users_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#refdata_#postgres_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#exclude2_products.sql");
        
        String path = getPath(parentFile);
        
        List<Script> actual = new ArrayList<Script>();
        
        scriptSource = new ResourceScriptSource();
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2");
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2");
        scriptSource.init(configuration);

        scriptSource.getScriptsAt(actual, path, nameFolder, "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }
        
        List<String> expected = new ArrayList<String>();
        expected.add("01_#include1_products.sql");
        expected.add("01_#include2_@users_products.sql");
        expected.add("@users_#include1_#include2_products.sql");
        expected.add("#include1_#include2_products.sql");
        expected.add("01_#include1_#include2_products.sql");
        
        //Assert.assertEquals(5, actual.size());
        ReflectionAssert.assertReflectionEquals(expected, actualNames, ReflectionComparatorMode.LENIENT_ORDER);
    }
    
    public String getPath(File file) {
        
        File baseFile = new File("target/test-classes/");
        String path = "";
        path = file.getAbsolutePath().substring(baseFile.getAbsolutePath().length());
        path = path.startsWith("\\") ? path.substring(1) : path;
        
        path = path.replace("\\", "/");
        
        return path;
    }
}
