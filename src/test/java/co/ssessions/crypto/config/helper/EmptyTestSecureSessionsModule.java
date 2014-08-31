package co.ssessions.crypto.config.helper;

import static org.mockito.Mockito.mock;
import co.ssessions.crypto.config.CryptoServiceConfiguration;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.modules.SecureSessionsModule;

import com.google.inject.TypeLiteral;

public class EmptyTestSecureSessionsModule extends SecureSessionsModule {

	public EmptyTestSecureSessionsModule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doBindSecureSessionsDAO() {
		SecureSessionsDAO secureSessionsDAOMock = mock(SecureSessionsDAO.class);
		bind(SecureSessionsDAO.class).to(secureSessionsDAOMock.getClass());
	}

	@Override
	public void doBindCryptoServiceConfiguration() {
		bind(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}).to(EmptyTestMapBasedCryptoServiceConfiguration.class);
	}
	
}
