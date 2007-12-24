/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

public class ResourceTranslatingClassLoaderTest {

	ResourceTranslatingClassLoader resourceTranslatingClassLoader;
	
	@Before
	public void init() {
		resourceTranslatingClassLoader = new ResourceTranslatingClassLoader("testSource.txt", "org/unitils/core/util/testTarget.txt");
	}
	
	@Test
	public void testGetResource() throws IOException {
		URL url = resourceTranslatingClassLoader.getResource("testSource.txt");
		
		// It should return a file from the classpath
		Assert.assertTrue(url.toString().startsWith("file:/"));
		// It must seam that the file actually resides at a location matching the requested path
		Assert.assertTrue(url.toString().endsWith("org/unitils/core/util/testTarget.txt"));
		
		// Verify that the file contents match the one of the target file
		URLConnection conn = url.openConnection();
		StringWriter sw = new StringWriter();
		IOUtils.copy(conn.getInputStream(), sw, "ISO-8859-1");
		ReflectionAssert.assertRefEquals("testtarget text", sw.toString());
	}
	
	@Test
	public void testGetResources() throws IOException {
		Enumeration<URL> urls = resourceTranslatingClassLoader.getResources("testSource.txt");
		Assert.assertTrue(urls.hasMoreElements());
		URL url = urls.nextElement();
		
		// It should return a file from the classpath
		Assert.assertTrue(url.toString().startsWith("file:/"));
		// It must seam that the file actually resides at a location matching the requested path
		Assert.assertTrue(url.toString().endsWith("org/unitils/core/util/testTarget.txt"));
		
		// Verify that the file contents match the one of the target file
		URLConnection conn = url.openConnection();
		StringWriter sw = new StringWriter();
		IOUtils.copy(conn.getInputStream(), sw, "ISO-8859-1");
		ReflectionAssert.assertRefEquals("testtarget text", sw.toString());
	}
	
	@Test
	public void testGetResource_TargetDoesNotExist() {
		resourceTranslatingClassLoader = new ResourceTranslatingClassLoader("testSource.txt", "unexistingTarget.txt");
		
		URL url = resourceTranslatingClassLoader.getResource("testSource.txt");
		Assert.assertNull(url);
	}
	
	@Test
	public void testGetResources_TargetDoesNotExist() throws IOException {
		resourceTranslatingClassLoader = new ResourceTranslatingClassLoader("testSource.txt", "unexistingTarget.txt");
		
		Enumeration<URL> urls = resourceTranslatingClassLoader.getResources("testSource.txt");
		Assert.assertFalse(urls.hasMoreElements());
	}
}
