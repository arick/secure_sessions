package co.ssessions.crypto.config.impl;

import co.ssessions.conf.EnvConfig;
import co.ssessions.crypto.config.AbstractMapBasedCryptoServiceConfiguration;

/**
 * The default configuration class for the default CryptoService implementation class.
 * This class expects that a property with the name "crypto.privateKeyFilePath" with some
 * String value associated with it.  The semantics of the key name implies that it's a filename 
 * and its directory path, but it can be whatever you need.<br/>
 * <br/>
 * <br/>
 * Author's Note: Because of the use of dependency injection, which I like a lot, it encourages instantiation of most classes via their constructors only. 
 * The CryptoService needs to pull configuration from some well known place that is NOT within the codebase. So I've chosen to 
 * pass the location of the location of the private key into this class via a System Property.  So long as the property "crypto.privateKeyFilePath" is
 * added to the global EnvironmentalConfig object then this approach is suitable.  As always please send me suggestions for improvements.<br/>
 * <br/>
 * <br/>
 * @author rashadmoore
 *
 */
public class DefaultCryptoServiceConfiguration extends AbstractMapBasedCryptoServiceConfiguration<String, String> {

	public DefaultCryptoServiceConfiguration() {
		super();
		this.params.put("crypto.privateKeyFilePath", EnvConfig.get("crypto.privateKeyFilePath"));
	}

}
