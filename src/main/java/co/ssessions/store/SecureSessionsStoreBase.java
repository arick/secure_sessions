package co.ssessions.store;

import org.apache.catalina.session.StoreBase;

public abstract class SecureSessionsStoreBase extends StoreBase {

	protected String applicationId;
	
	
	public String getApplicationId() {
		return this.applicationId;
	}

	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

}
