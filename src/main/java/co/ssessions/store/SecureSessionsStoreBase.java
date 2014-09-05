package co.ssessions.store;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.catalina.session.StoreBase;

public abstract class SecureSessionsStoreBase extends StoreBase {

	protected String applicationId;
	
	
	public String getApplicationId() {
		return this.applicationId;
	}

	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	
	/**
	 * 
	 * 
	 * @param httpSession
	 * @throws IOException
	 */
	public abstract void save(HttpSession httpSession) throws IOException;
	
	
	
	
}
