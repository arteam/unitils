package org.unitils.dbmaintainer.locator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

/**
 * Will test finding data on the classpath
 *
 * @author tdr
 *
 * @since 1.0.2
 *
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SuppressWarnings("unchecked")
public class ClassPathDataLocatorTest {

    @TestedObject
    ClassPathDataLocator classPathDataLocator;
    
    @Mock
    private ResourcePickingStrategie resourcePickingStrategie;

    private List<URL> urlList;

    private List<URL> urlResultList;

    String resourceName;

    /*** */
    @Before
    public void setUp(){

        resourceName = "/org/unitils/testdata/exampleResourceData.xml";



        urlList = new ArrayList<URL>();
        URL url = getClass().getResource(resourceName);
        Assert.assertNotNull("Resource '"+resourceName+"' not found error in testSetup", url);
        urlList.add(url);


        urlResultList = new ArrayList<URL>();
        urlResultList.add(url);
    }

    /*** */
    @Test
    public void getDataResourceTestAbsolutePath(){

        EasyMock.expect(resourcePickingStrategie.filter(ListUtils.EMPTY_LIST,resourceName)).andReturn(urlResultList);

        EasyMockUnitils.replay();

        InputStream is = classPathDataLocator.getDataResource(resourceName, resourcePickingStrategie  );
        Assert.assertNotNull(is);
    }

    /*** */
    @Test
    public void getDataResourceTestRelativePath(){

        EasyMock.expect(resourcePickingStrategie.filter((List<URL>) EasyMock.anyObject() ,(String)EasyMock.anyObject())).andReturn(urlResultList);

        EasyMockUnitils.replay();

        InputStream is = classPathDataLocator.getDataResource(resourceName.substring(1), resourcePickingStrategie  );
        Assert.assertNotNull(is);
    }

    /*** */
    @Test
    public void getDataResourceTestNonExisting(){

        EasyMock.expect(resourcePickingStrategie.filter((List<URL>) EasyMock.anyObject() ,(String)EasyMock.anyObject())).andReturn(ListUtils.EMPTY_LIST);

        EasyMockUnitils.replay();

        InputStream is = classPathDataLocator.getDataResource(resourceName.substring(0, resourceName.length()-2).concat("bla"), resourcePickingStrategie  );
        Assert.assertNull(is);
    }

    @Test
    public void testGetDataSourceIOException() throws IOException {
        List<URL> resourcesF = new ArrayList<URL>();
        URL url = new URL("https://graph.facebook.com/me");
        resourcesF.add(url );
        EasyMock.expect(resourcePickingStrategie.filter((List<URL>) EasyMock.anyObject() ,(String)EasyMock.anyObject())).andReturn(resourcesF);
        //EasyMock.expect(url.openStream()).andThrow(new IOException());

        EasyMockUnitils.replay();

        InputStream is = classPathDataLocator.getDataResource(resourceName.substring(0, resourceName.length()-2).concat("bla"), resourcePickingStrategie  );
        Assert.assertNull(is);

    }


}
