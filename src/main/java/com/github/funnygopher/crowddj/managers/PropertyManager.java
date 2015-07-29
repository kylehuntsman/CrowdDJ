package com.github.funnygopher.crowddj.managers;

import com.github.funnygopher.crowddj.Property;

import java.io.*;
import java.util.Properties;

public class PropertyManager {

	public final int PORT = 8081;
	public final String DB_USERNAME = DatabaseManager.USERNAME;
	public final String DB_PASSWORD = DatabaseManager.PASSWORD;

	private Properties properties;
	private String filename;

	public PropertyManager(String filename) {
		this.filename = filename;
		properties = loadPropertiesFromFile(filename);
	}

	public String getStringProperty(Property property) {
		return properties.getProperty(property.getValue());
	}

	public int getIntProperty(Property property) {
		String value = properties.getProperty(property.getValue());
		return Integer.valueOf(value);
	}

	public void saveProperties() {
		savePropertiesFile(filename, properties);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void setProperty(Property property, String value) {
		setProperty(property.getValue(), value);
	}

	private InputStream getPropertiesFileStream(String filename) {
		InputStream input;

		// Try to find the properties file in the current directory
		try {
			File file = new File(filename);
			input = new FileInputStream(file);
		} catch(FileNotFoundException e) {
			input = null;
		}
		return input;
	}

	private Properties loadPropertiesFromFile(String filename) {
		Properties properties = new Properties();
		InputStream input;

		// Try to find the properties file in the current directory
		input = getPropertiesFileStream(filename);

		try {
			if(input != null) {
                // Load the properties from the file
                input = getPropertiesFileStream(filename);
                properties.load(input);
			} else {
                // Save a default properties file
                properties = getDefaultProperties();
                savePropertiesFile(filename, properties);
            }

		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	private void savePropertiesFile(String filename, Properties properties) {
		try {
			File file = new File(filename);
			OutputStream output = new FileOutputStream(file);
			properties.store(output, "Configuration properties for the CrowdDJ Desktop Application");
            output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Properties getDefaultProperties() {
		Properties properties = new Properties();

		// Sets default values for CrowdDJ
		properties.setProperty(Property.PORT.getValue(), String.valueOf(PORT));

		// Sets the default values for the database
		properties.setProperty(Property.DB_USERNAME.getValue(), DB_USERNAME);
		properties.setProperty(Property.DB_PASSWORD.getValue(), DB_PASSWORD);

		return properties;
	}
}
