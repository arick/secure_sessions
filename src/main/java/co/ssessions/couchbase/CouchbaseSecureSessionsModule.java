package co.ssessions.couchbase;

import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.modules.SecureSessionsModule;

public class CouchbaseSecureSessionsModule extends SecureSessionsModule {

	public void doBindSecureSessionsDAO() {
		bind(SecureSessionsDAO.class).to(CouchbaseDAO.class);
	}

}
