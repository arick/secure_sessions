package co.ssessions.conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import co.ssessions.util.SecureSessionsConstants;


public class EnvConfig {

	private static volatile PropertiesConfiguration singleton = null;
	
	
	public EnvConfig() {
		// Intentionally empty constructor
	}
	
	public final PropertiesConfiguration getInstance() {
		PropertiesConfiguration proxy = singleton;
		if (proxy == null) {
			synchronized (this) {
				proxy = singleton;
				if (proxy == null) {
					singleton = proxy = this.init();
				}
			}
		}
		return proxy;
	}
	
	
	public final void nullifySingleton() {
		singleton = null;
	}
	
	public static void clearSingleton() {
		new EnvConfig().nullifySingleton();
	}
	
	private PropertiesConfiguration init() {

		PropertiesConfiguration config = new PropertiesConfiguration();
		
		try {
			
			String buildEnvironment = SecureSessionsConstants.DEFAULT_BUILD_ENVIRONMENT;
			if(StringUtils.isNotBlank(System.getProperty(SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_NAME))) {
				buildEnvironment = System.getProperty(SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_NAME);
			}
			// Get the common build properties file
			config.load(SecureSessionsConstants.COMMON_BUILD_ENVIRONMENT + SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_FILENAME_SUFFIX);

			config.load(buildEnvironment + SecureSessionsConstants.BUILD_ENVIRONMENT_PROPERTY_FILENAME_SUFFIX);

		} catch (ConfigurationException ce) {
			ce.printStackTrace();
		}
		
		return config;
		
	} // END init Method
	
	
	public static PropertiesConfiguration load(String propertiesFilePath) {
		
		if (StringUtils.isBlank(propertiesFilePath)) {
			throw new IllegalArgumentException("ERROR: In EnvConfig.load(String propertiesFilePath) propertiesFilePath is blank.");
		}
		
		PropertiesConfiguration config = new EnvConfig().getInstance();
		
		try {
			config.load(propertiesFilePath);
		} catch (ConfigurationException ce) {
			// TODO: Fix exception handling
			ce.printStackTrace();
		}
		
		return config;
	}
	
	
	public static Configuration get() {
		return new EnvConfig().getInstance();
	}
	
	
	public static String get(String property) {
		
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException("ERROR: In EnvConfig.get(String property) property is blank.");
		}
		
		PropertiesConfiguration config = new EnvConfig().getInstance();
		String value = config.getString(property);
		return value;
	}
	
	
	public static String getSafe(String property) {
		
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException("ERROR: In EnvConfig.get(String property) property is blank.");
		}
		
		String value = EnvConfig.get(property);
		if (StringUtils.isBlank(value)) {
			return "";
		} else {
			return value;
		}
	}
	
	
	public static String[] getStringArray(String property) {

		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException("ERROR: In EnvConfig.getStringArray(String property) property is blank.");
		}
		
		String[] values = new EnvConfig().getInstance().getStringArray(property);
		
		if (values == null) {
			return null;
		} else if ((values != null) && (values.length == 0)) {
			return null;
		} else if ((values != null) && (values.length == 1) && ("".equalsIgnoreCase(values[0]))) {
			return new String[]{};
		} 
		return values;
	}
	
	
} // END EnvironmentConfiguration Class
