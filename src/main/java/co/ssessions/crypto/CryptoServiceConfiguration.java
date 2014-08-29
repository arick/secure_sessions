package co.ssessions.crypto;

import java.util.Set;

public interface CryptoServiceConfiguration {

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public abstract String get(String parameter);
	
	
	/**
	 * 
	 * @return
	 */
	public abstract Set<String> getParameterNames();
	
	
}
