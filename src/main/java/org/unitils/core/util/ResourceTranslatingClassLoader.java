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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.unitils.util.ReflectionUtils;

/**
 * ClassLoader implementation that is able to trick the JVM into believing that a certain resource is available in the classpath 
 * under a certain name. But actually, this resource is stored in the classpath under a different name, but in the same jar or
 * classes directory.
 * 
 * This class was written to be able to modify the behavior of an external library (Hibernate EntityManager) that written such that 
 * it's impossible to get it to load a custom resource any other way. This is a hack and should be used with extreme caution! 
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ResourceTranslatingClassLoader extends ClassLoader {

	
	/**
	 * Map that contains all resource translations that this ClassLoader currenlty supports
	 */
	private Map<String, String> resourceTranslationMap = new HashMap<String, String>();
	
	
	public ResourceTranslatingClassLoader(String... resourceTranslations) {
		for (int i = 0; i < resourceTranslations.length; i += 2) {
			resourceTranslationMap.put(resourceTranslations[i], resourceTranslations[i + 1]);
		}
	}
	
	/**
	 * @See ClassLoader#getResources(String)
	 * 
	 * If the given resource name was registered using {@link #registerResourceTranslation(String, String)}, a single
	 * URL is returned, which appears to be the requested resource, but contains the content of the translated resource.
	 */
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (resourceTranslationMap.containsKey(name)) {
			final URL targetUrl = getTranslatedResource(name);
			
			return new Enumeration<URL>() {
				
				boolean accessed = false;
				
				public boolean hasMoreElements() {
					return targetUrl != null && !accessed;
				}
				
				public URL nextElement() {
					accessed = true;
					return targetUrl;
				}
			};
		}
		return super.getResources(name);
	}

	/**
	 * @See ClassLoader#getResources(String)
	 * 
	 * If the given resource name was registered using {@link #registerResourceTranslation(String, String)}, a 
	 * URL is returned, which appears to be the requested resource, but contains the content of the translated resource.
	 */
	@Override
	public URL getResource(String name) {
		if (resourceTranslationMap.containsKey(name)) {
			return getTranslatedResource(name);
		}
		return super.getResource(name);
	}

	
	/**
	 * Given the source resource name, returns a URL that appears to be the requested resource, but actually links
	 * to the translated resource.
	 * 
	 * @param name
	 * @return a URL that appears to be the requested resource, but actually links to the translated resource.
	 */
	private URL getTranslatedResource(String name) {
		String translatedResourceName = resourceTranslationMap.get(name);
		URL targetUrl = super.getResource(translatedResourceName);
		if (targetUrl == null) {
			return null;
		}
		
		URL sourceUrl = super.getResource(translatedResourceName);
		Field fileField = ReflectionUtils.getFieldWithName(URL.class, "file", false);
		ReflectionUtils.setFieldValue(targetUrl, fileField, replaceWithSource(name, translatedResourceName, targetUrl.getFile()));
		Field handlerField = ReflectionUtils.getFieldWithName(URL.class, "handler", false);
		URLStreamHandler urlStreamHandler = ReflectionUtils.getFieldValue(targetUrl, handlerField);
		ReflectionUtils.setFieldValue(targetUrl, handlerField, new URLSourceObfiscatingURLStreamHandler(urlStreamHandler, sourceUrl));
		return targetUrl;
	}

	
	private String replaceWithSource(String translationFrom, String translationTo, String fileName) {
		String partBeforeTranslationTo = StringUtils.substringBeforeLast(fileName, translationTo);
		return partBeforeTranslationTo + translationFrom;
	}
	
	private static class URLSourceObfiscatingURLStreamHandler extends URLStreamHandler {

		private URLStreamHandler delegate;
		
		private URL sourceUrl;
		
		
		
		protected URLSourceObfiscatingURLStreamHandler(
				URLStreamHandler delegate, URL sourceUrl) {
			super();
			this.delegate = delegate;
			this.sourceUrl = sourceUrl;
		}



		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			Method openConnectionMethod = ReflectionUtils.getMethod(URLStreamHandler.class, "openConnection", URL.class);
			return ReflectionUtils.invokeMethodSilent(delegate, openConnectionMethod, sourceUrl);
		}
		
	}
	
}
