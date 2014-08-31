package co.ssessions.crypto.config.helper;

import co.ssessions.crypto.config.AbstractMapBasedCryptoServiceConfiguration;

public class EmptyTestMapBasedCryptoServiceConfiguration extends AbstractMapBasedCryptoServiceConfiguration<String, String> {

	public EmptyTestMapBasedCryptoServiceConfiguration() {
		super();
		// Left blank to test for empty parameter map behavior
	}

}
