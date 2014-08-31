package co.ssessions.crypto.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.executable.ValidateOnExecution;

/**
 * 
 * This class is designed to be extended by overriding the constructor and providing the object's basic configuration state upon the instantiation of the class.
 * 
 * @author rashadmoore
 *
 */
public abstract class AbstractMapBasedCryptoServiceConfiguration<K, V> implements CryptoServiceConfiguration<K, V> {

	protected Map<K, V> params;
	
	public AbstractMapBasedCryptoServiceConfiguration() {
		
		this.params = new HashMap<K, V>();
	}
	
	@ValidateOnExecution
	@Override
	public V get(K parameter) {
		return this.params.get(parameter);
	}
	
	
	@Override
	public Set<K> getParameterNames() {
		return this.params.keySet();
	}
	
}
