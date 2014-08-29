package co.ssessions.crypto;

import javax.validation.executable.ValidateOnExecution;

import co.ssessions.util.MethodParameterValidationInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class TestPKCS8CryptoModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(CryptoService.class).to(PKCS8CryptoService.class);
		bind(CryptoServiceConfiguration.class).to(DefaultCryptoServiceConfiguration.class);
		
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), new MethodParameterValidationInterceptor());
		
	}
	
	
}
