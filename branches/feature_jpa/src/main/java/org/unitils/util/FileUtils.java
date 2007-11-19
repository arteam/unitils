package org.unitils.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.integrationtest.UnitilsIntegrationTest;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

public class FileUtils {

	public static void copyClassPathResource(String classPathResourceName, String fileSystemDirectoryName) {
		InputStream resourceInputStream = null;
		OutputStream fileOutputStream = null;
		try {
			resourceInputStream = UnitilsIntegrationTest.class.getResourceAsStream(classPathResourceName);
			String fileName = StringUtils.substringAfterLast(classPathResourceName, "/");
			File fileSystemDirectory = new File(fileSystemDirectoryName);
			fileSystemDirectory.mkdirs();
			fileOutputStream = new FileOutputStream(fileSystemDirectoryName + "/" + fileName);
			IOUtils.copy(resourceInputStream, fileOutputStream);
		} catch (IOException e) {
			throw new UnitilsException(e);
		} finally {
			IOUtils.closeQuietly(resourceInputStream);
			IOUtils.closeQuietly(fileOutputStream);
		}
	}
}
