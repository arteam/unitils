package org.unitils.build;

import java.util.SortedSet;
import java.util.TreeSet;

public class ClassUsageLoggingClassLoader extends ClassLoader {

	private static SortedSet<String> loadedClasses = new TreeSet<String>();
	
	private static SortedSet<String> foundClasses = new TreeSet<String>();
	
	
	
	public ClassUsageLoggingClassLoader() {
		super();
	}


	public ClassUsageLoggingClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		loadedClasses.add(name);
		return super.loadClass(name);
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		loadedClasses.add(name);
		return super.loadClass(name, resolve);
	}
	

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		foundClasses.add(name);
		return super.findClass(name);
	}


	public static SortedSet<String> getLoadedClasses() {
		return loadedClasses;
	}


	public static SortedSet<String> getFoundClasses() {
		return foundClasses;
	}
	
	
}
