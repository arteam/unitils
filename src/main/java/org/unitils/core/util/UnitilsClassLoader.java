package org.unitils.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class UnitilsClassLoader extends ClassLoader {

	private Map<String, String> resourceTranslationMap = new HashMap<String, String>();
	
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (resourceTranslationMap.containsKey(name)) {
			return super.getResources(resourceTranslationMap.get(name));
		}
		return super.getResources(name);
	}

	@Override
	public URL getResource(String name) {
		if (resourceTranslationMap.containsKey(name)) {
			return super.getResource(resourceTranslationMap.get(name));
		}
		return super.getResource(name);
	}

	
	public void registerResourceTranslation(String from, String to) {
		resourceTranslationMap.put(from, to);
	}
	
	
	public void unregisterResourceTranslation(String from) {
		resourceTranslationMap.remove(from);
	}
	
}
