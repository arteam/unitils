package org.unitils.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ClassUsageFinder {

	private static final String CLASS_BASE_FOLDER = "C:/Projects/unitils/lib/spring";

	private static final String TEMP_FOLDER = "C:/Temp";

	private static final String TEST_RESULT_FILE = "C:/Temp/springclassusagetest/testresult.txt";

	private static final String INTEGRATIONTEST_EXECUTABLE = "C:/Projects/unitils/runIntegrationTest.bat";

	private static final String CLASS_USAGE_LOG_FILE = "C:/Projects/unitils/springClassUsage.txt";

	private PrintWriter logFileWriter;

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException {
		new ClassUsageFinder().detectClassUsage();
	}

	@SuppressWarnings("unchecked")
	public void detectClassUsage() throws InterruptedException, IOException {
		openLogFileWriter();
		Collection<File> files = FileUtils.listFiles(
				new File(CLASS_BASE_FOLDER), null, true);
		for (File file : files) {
			System.out.println("Checking file " + file);
			
			File tempFile = getTempFile(file);
			file.renameTo(tempFile);
			runIntegrationTest();
			if (!isTestSucceeded()) {
				logClassUsage(getClassFromFile(file));
			}
			tempFile.renameTo(file);
		}
		closeLogFileWriter();
	}

	private String getClassFromFile(File file) {
		String absolutePath = file.getAbsolutePath();
		String pathPrefix = new File(CLASS_BASE_FOLDER).getAbsolutePath();
		String relativePath = StringUtils.removeStart(absolutePath,
				pathPrefix);
		String className = StringUtils.removeEnd(StringUtils.replaceChars(relativePath, File.separatorChar, '.'), ".class");
		return className;
	}

	private void closeLogFileWriter() {
		logFileWriter.close();
	}

	private void openLogFileWriter() throws FileNotFoundException {
		logFileWriter = new PrintWriter(CLASS_USAGE_LOG_FILE);
	}

	private void logClassUsage(String className) {
		logFileWriter.println(className);
		logFileWriter.flush();
	}

	private File getTempFile(File file) {
		return new File(TEMP_FOLDER + "/" + file.getName());
	}

	private boolean isTestSucceeded() throws IOException {
		String result = FileUtils.readFileToString(new File(TEST_RESULT_FILE));
		boolean succeeded = Boolean.valueOf(result);
		return succeeded;
	}

	private void runIntegrationTest() throws InterruptedException, IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(INTEGRATIONTEST_EXECUTABLE);
        StreamGobbler errorGobbler = new 
            StreamGobbler(proc.getErrorStream(), "ERROR");            
        StreamGobbler outputGobbler = new 
            StreamGobbler(proc.getInputStream(), "OUTPUT");
        errorGobbler.start();
        outputGobbler.start();
        proc.waitFor();
	}

	class StreamGobbler extends Thread {
		InputStream is;
		String type;

		StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				while (br.readLine() != null);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
