package org.unitils.io.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class StringConversionStrategy implements ConversionStrategy<String> {

	public String readContent(InputStream inputStream, String encoding) throws IOException {

		StringWriter writer = new StringWriter();
		InputStreamReader in = new InputStreamReader(inputStream, encoding);

		IOUtils.copy(in, writer);
		return writer.toString();
	}

	public String getDefaultPostFix() {
		return "txt";
	}

	public Class<String> getDefaultEndClass() {
		return String.class;
	}

}
