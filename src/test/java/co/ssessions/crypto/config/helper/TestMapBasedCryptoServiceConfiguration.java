package co.ssessions.crypto.config.helper;

import co.ssessions.crypto.config.AbstractMapBasedCryptoServiceConfiguration;

public class TestMapBasedCryptoServiceConfiguration extends AbstractMapBasedCryptoServiceConfiguration<String, String> {

	public TestMapBasedCryptoServiceConfiguration() {
		super();
		this.params.put("tester.crypto.someKey1", "someValueOne");
		this.params.put("tester.crypto.someKey2", "someValueTwo");
		this.params.put("tester.crypto.someKey3", "someValueThree");
	}

}
