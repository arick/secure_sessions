package co.ssessions.conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

/**
 * Simple shorter alias for the EnvironmentConfiguration class
 * 
 * @author rashadmoore
 *
 */
public class EnvConfig {

	public static Configuration get() {
		return EnvironmentConfiguration.get();
	}
	
	
	public static String get(String property) {
		
		return EnvConfig.get().getString(property);
	}
	
	
	public static String getSafe(String property) {
		
		String value = EnvConfig.get(property);
		if (StringUtils.isBlank(value)) {
			return "";
		} else {
			return value;
		}
	}
	
	
	public static String[] getStringArray(String property) {
		
		return EnvironmentConfiguration.get().getStringArray(property);
	}
	
}
