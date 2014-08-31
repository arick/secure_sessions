package co.ssessions.crypto.config;

import java.util.Set;

/**
 * Implementation interface for all CryptoService configuration strategies.
 * 
 * @author rashadmoore
 *
 */
public interface CryptoServiceConfiguration<K, V> {

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public abstract V get(K parameter);
	
	
	/**
	 * 
	 * @return
	 */
	public abstract Set<K> getParameterNames();
	
	
}
