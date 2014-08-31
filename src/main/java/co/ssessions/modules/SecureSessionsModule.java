package co.ssessions.modules;

import javax.validation.executable.ValidateOnExecution;

import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.config.CryptoServiceConfiguration;
import co.ssessions.crypto.config.impl.DefaultCryptoServiceConfiguration;
import co.ssessions.crypto.impl.PKCS8CryptoService;
import co.ssessions.store.DefaultSecureSessionsStoreBase;
import co.ssessions.store.SecureSessionsStoreBase;
import co.ssessions.util.MethodParameterValidationInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

public abstract class SecureSessionsModule extends AbstractModule {

	@Override
	protected void configure() {
		
		this.doBindInterceptors();
		
		this.doBindSecureSessionsStoreBase();
		
		this.doBindCryptoService();
		
		this.doBindCryptoServiceConfiguration();
		
		this.doBindSecureSessionsDAO();
		
	}
	
	public void doBindInterceptors() {
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), new MethodParameterValidationInterceptor());
	}

	public void doBindSecureSessionsStoreBase() {
		bind(SecureSessionsStoreBase.class).to(DefaultSecureSessionsStoreBase.class);
	}
	
	public void doBindCryptoService() {
		
		bind(CryptoService.class).to(PKCS8CryptoService.class);
	}
	
	public void doBindCryptoServiceConfiguration() {
		bind(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}).to(DefaultCryptoServiceConfiguration.class);
	}
	
	public abstract void doBindSecureSessionsDAO();
	
	
}
