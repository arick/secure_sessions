package co.ssessions.crypto.config.impl.helper;

import static org.mockito.Mockito.mock;
import co.ssessions.crypto.config.CryptoServiceConfiguration;
import co.ssessions.crypto.config.impl.DefaultCryptoServiceConfiguration;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.modules.SecureSessionsModule;

import com.google.inject.TypeLiteral;

public class TestDefaultSecureSessionsModule extends SecureSessionsModule {

	public TestDefaultSecureSessionsModule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doBindSecureSessionsDAO() {
		SecureSessionsDAO secureSessionsDAOMock = mock(SecureSessionsDAO.class);
		bind(SecureSessionsDAO.class).to(secureSessionsDAOMock.getClass());
	}

	@Override
	public void doBindCryptoServiceConfiguration() {
		bind(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}).to(DefaultCryptoServiceConfiguration.class);
	}
	
}
