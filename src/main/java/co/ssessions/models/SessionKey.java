package co.ssessions.models;

public class SessionKey {

	private String applicationId = "UNKNOWN";
	private String sessionId;
	private String delimiter = "~~";

	public SessionKey() {
	}

	public SessionKey(String sessionId) {
		this.sessionId = sessionId;
	}

	public SessionKey(String applicationId, String sessionId) {
		this.applicationId = applicationId;
		this.sessionId = sessionId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {

		// TODO: Defensive Programming Here.
		// Check to see if the object is populated before creating the SessionKey
		
		StringBuffer buff = new StringBuffer();
		buff.append(this.applicationId);
		buff.append(this.delimiter);
		buff.append(this.sessionId);

		return buff.toString();
	}

	public SessionKey fromString(String sessionKeyString) {
		
		// TODO: Defensive Programming Here.

		String[] sessionKeyArray = sessionKeyString.split(this.delimiter);
		this.setApplicationId(sessionKeyArray[0]);
		this.setSessionId(sessionKeyArray[1]);
		return this;

	}

	public String getDelimiter() {
		return delimiter;
	}

} // END SessionKey Class
