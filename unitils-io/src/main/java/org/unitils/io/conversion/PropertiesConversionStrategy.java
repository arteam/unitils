package org.unitils.io.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class PropertiesConversionStrategy implements ConversionStrategy<Properties> {

	public Properties readContent(InputStream inputStream, String encoding) throws IOException {
		Properties result = new Properties();
		result.load(new InputStreamReader(inputStream, encoding));
		return result;
	}

	public String getDefaultPostFix() {
		return "properties";
	}

	public Class<Properties> getDefaultEndClass() {
		return Properties.class;
	}

}
