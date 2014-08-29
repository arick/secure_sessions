package co.ssessions.modules;

import javax.validation.executable.ValidateOnExecution;

import co.ssessions.couchbase.CouchbaseDAO;
import co.ssessions.couchbase.CouchbaseSessionStore;
import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.CryptoServiceConfiguration;
import co.ssessions.crypto.DefaultCryptoServiceConfiguration;
import co.ssessions.crypto.PKCS8CryptoService;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.store.SecureSessionsStoreBase;
import co.ssessions.util.MethodParameterValidationInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class CouchbaseSecureSessionsModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(SecureSessionsDAO.class).to(CouchbaseDAO.class);
		
		bind(CryptoService.class).to(PKCS8CryptoService.class);

		bind(SecureSessionsStoreBase.class).to(CouchbaseSessionStore.class);
		
		bind(CryptoServiceConfiguration.class).to(DefaultCryptoServiceConfiguration.class);
		
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), new MethodParameterValidationInterceptor());
		
	}
	
}
