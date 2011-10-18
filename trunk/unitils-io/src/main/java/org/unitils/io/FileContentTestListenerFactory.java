package org.unitils.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.util.ReflectionUtils;

public class FileContentTestListenerFactory {

	private static final String CONVERSION_STRATEGY_KEY = "org.unitils.io.conversion";
	private static final String READER_STRATEGY_KEY = "org.unitils.io.reader";

	public static FileContentTestListener createFileContentTestListener(
			Properties properties) {

		FileContentTestListener result = new FileContentTestListener();
		HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap = new HashMap<Object, ConversionStrategy<?>>();

		List<ConversionStrategy<?>> strategies = resolveConverstionStrategies(properties);

		for (ConversionStrategy<?> tmp : strategies) {
			conversionStrategiesMap.put(tmp.getDefaultEndClass(), tmp);
		}

		result.setConversionStrategiesMap(conversionStrategiesMap);
		result.setDefaultReadingStrategy(resolveReadingStrategy(properties));
		return result;
	}

	private static ReadingStrategy resolveReadingStrategy(Properties properties) {
		String className = properties.getProperty(READER_STRATEGY_KEY);
		return ReflectionUtils.createInstanceOfType(className.trim(), false);
	}

	private static List<ConversionStrategy<?>> resolveConverstionStrategies(
			Properties properties) {
		String conversionStrategiesString = properties
				.getProperty(CONVERSION_STRATEGY_KEY);

		String[] split = conversionStrategiesString.split(",");

		List<ConversionStrategy<?>> result = new ArrayList<ConversionStrategy<?>>(
				split.length);

		for (String className : split) {
			ConversionStrategy<?> conversionStrategy = ReflectionUtils
					.createInstanceOfType(className.trim(), false);
			result.add(conversionStrategy);
		}

		return result;
	}
}
