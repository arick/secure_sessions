package co.ssessions.conf;

import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import co.ssessions.util.SecureSessionsConstants;

public class EnvironmentConfiguration {

	private static volatile Configuration config;

	public static Configuration get() {
		Configuration result = config;
		if (result == null) {
			synchronized (EnvironmentConfiguration.class) {
				result = config;
				if (result == null) {
					config = result = init();
				}
			}
		}
		return result;
	}
	
	
	public static Configuration init() {

		PropertiesConfiguration config = new PropertiesConfiguration();
		
		try {
			
			String buildEnvironment = SecureSessionsConstants.DEFAULT_BUILD_ENVIRONMENT;
			if(StringUtils.isNotBlank(System.getProperty(SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_NAME))) {
				buildEnvironment = System.getProperty(SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_NAME);
			}
			// Get the common build properties file
			config.load(SecureSessionsConstants.COMMON_BUILD_ENVIRONMENT + SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_FILENAME_SUFFIX);

			config.load(buildEnvironment + SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_FILENAME_SUFFIX);


		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		return config;
		
	} // END init Method
	
	
	public static String get(String property) {
		Configuration config = EnvironmentConfiguration.get();
		String value = config.getString(property);
		return value;
	}
	
	
} // END EnvironmentConfiguration Class
