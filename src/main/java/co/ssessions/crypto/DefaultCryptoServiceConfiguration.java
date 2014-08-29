package co.ssessions.crypto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import co.ssessions.conf.EnvConfig;

import com.google.inject.Inject;

public class DefaultCryptoServiceConfiguration extends AbstractCryptoServiceConfiguration {

	private Map<String, String> params;
	
	@Inject
	public DefaultCryptoServiceConfiguration() {
		
		this.params = new HashMap<String, String> ();
		this.params.put("crypto.privateKeyFilePath", EnvConfig.get("crypto.privateKeyFilePath"));
		
	}
	
	
	@Override
	public String get(String parameter) {
		return this.params.get(parameter);
	}
	
	
	@Override
	public Set<String> getParameterNames() {
		return this.params.keySet();
	}

}
