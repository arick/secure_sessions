package co.ssessions.crypto.config;

import java.util.Set;

import javax.validation.constraints.NotNull;

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
	public abstract V get(@NotNull K parameter);
	
	
	/**
	 * 
	 * @return
	 */
	public abstract Set<K> getParameterNames();
	
	
}
