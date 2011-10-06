package org.unitils.io.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public interface ReadingStrategy {
	public InputStream handleFile(Field field, Object testObject, String extension) throws IOException;
}
