package co.ssessions.dao;

import java.util.Set;

import co.ssessions.models.SessionKey;
import co.ssessions.models.SessionModel;

import com.couchbase.client.CouchbaseClient;

public interface SecureSessionsDAO {

	/**
	 * 
	 * @param sessionKey
	 * @param sessionModel
	 */
	public abstract void storeSession(SessionKey sessionKey, SessionModel sessionModel);

	
	/**
	 * 
	 * @param sessionKey
	 */
	public abstract void deleteSession(SessionKey sessionKey);

	
	/**
	 * 
	 * @param sessionKeys
	 */
	public abstract void deleteSessions(Set<SessionKey> sessionKeys);

	
	/**
	 * 
	 * @param sessionKey
	 * @return
	 */
	public abstract int numberOfSessions(SessionKey sessionKey);

	
	/**
	 * 
	 * @param sessionKey
	 * @return
	 */
	public abstract SessionModel retrieveSession(SessionKey sessionKey);

} 