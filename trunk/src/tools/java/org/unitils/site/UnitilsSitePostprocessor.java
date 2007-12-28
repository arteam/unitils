package org.unitils.site;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;

public class UnitilsSitePostprocessor {

	private static final Logger logger = Logger.getLogger(UnitilsSitePostprocessor.class);
	
	public static final String GOOGLE_ANALYTICS_JAVASCRIPT = 
		"<script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\">\n" +
		"</script>\n" +
		"<script type=\"text/javascript\">\n" +
		"_uacct = \"UA-2978532-1\";\n" +
		"urchinTracker();\n" +
		"</script>\n";
	
	private String siteDirName;
	
	public UnitilsSitePostprocessor(String siteDirName) {
		this.siteDirName = siteDirName;
	}
	
	@SuppressWarnings("unchecked")
	public void postProcessSite() throws IOException {
		File siteDir = new File(siteDirName);
		if (!siteDir.exists() || !siteDir.isDirectory()) {
			throw new UnitilsException("Site directory should be an existing, non-empty directory");
		}
		
		Collection<File> files = FileUtils.listFiles(siteDir, new String[] {"html", "htm"}, true);
		for (File file : files) {
			addGoogleAnalyticsJavascript(file);
		}
	}

	public void addGoogleAnalyticsJavascript(File file) throws IOException {
		logger.info("Adding google analytics javascript to " + file);
		
		String content = FileUtils.readFileToString(file);
		Pattern p = Pattern.compile("</body>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		StringBuffer newContentBuffer = new StringBuffer();
		if (m.find()) {
			m.appendReplacement(newContentBuffer, "</body>\n\n" + GOOGLE_ANALYTICS_JAVASCRIPT);
			m.appendTail(newContentBuffer);
		} else {
			logger.error("Could not find </body> tag in file " + file.getAbsolutePath());
		}
		FileUtils.writeStringToFile(file, newContentBuffer.toString());
	}

	public static void main(String[] args) throws Exception {
		String siteDir = args[0];
		new UnitilsSitePostprocessor(siteDir).postProcessSite();
	}
	
}
