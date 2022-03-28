package org.cloudbus.cloudsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public enum ExperimentConfiguration {

	INSTANCE;

	private static Properties properties = System.getProperties();

	static {
		loadPropertiesFromFile("experiment.properties");
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	private static void loadPropertiesFromFile(String file) {
		File propertiesFile = new File(file);
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}