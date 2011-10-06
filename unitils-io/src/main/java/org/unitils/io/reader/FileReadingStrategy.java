package org.unitils.io.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class FileReadingStrategy implements ReadingStrategy {
	public InputStream handleFile(Field field, Object testObject, String extension) throws IOException {

		String fileName = null;
		FileContent annotation = field.getAnnotation(FileContent.class);

		if (annotation.location().isEmpty()) {
			fileName = prefixPackageNameFilePath(testObject.getClass(), resolveFileName(testObject, extension));

		} else {
			fileName = annotation.location();
		}

		InputStream result = this.getClass().getClassLoader().getResourceAsStream(fileName);
		if (result == null) {
			throw new UnitilsException(fileName + " not found.");
		}

		return result;

	}

	protected String resolveFileName(Object testObject, String extension) {
		// TODO code is almost the same as in the DbunitModule at line
		// 448, so this should be refactored.
		String fileName;
		String className = testObject.getClass().getName();
		fileName = className.substring(className.lastIndexOf(".") + 1, className.length()) + '.' + extension;
		return fileName;
	}

	protected String prefixPackageNameFilePath(Class<?> testClass, String fileName) {
		String className = testClass.getName();
		int indexOfLastDot = className.lastIndexOf('.');
		if (indexOfLastDot == -1) {
			return fileName;
		}

		String packageName = className.substring(0, indexOfLastDot).replace('.', '/');
		return packageName + '/' + fileName;
	}
}