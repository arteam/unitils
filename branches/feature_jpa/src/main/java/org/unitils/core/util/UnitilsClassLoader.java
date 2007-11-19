package org.unitils.core.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UnitilsClassLoader extends ClassLoader {

	private Map<String, String> resourceTranslationMap = new HashMap<String, String>();
	
	
	@Override
	protected URL findResource(String name) {
		if (resourceTranslationMap.containsKey(name)) {
			return super.findResource(resourceTranslationMap.get(name));
		}
		return super.findResource(name);
	}

	
	public void registerResourceTranslation(String from, String to) {
		resourceTranslationMap.put(from, to);
	}
	
	
	public void unregisterResourceTranslation(String from) {
		resourceTranslationMap.remove(from);
	}
	
}
