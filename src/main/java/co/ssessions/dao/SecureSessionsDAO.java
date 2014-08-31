package co.ssessions.dao;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.executable.ValidateOnExecution;

import org.hibernate.validator.constraints.NotEmpty;

import co.ssessions.models.SessionKey;
import co.ssessions.models.SessionModel;

import com.couchbase.client.CouchbaseClient;

public interface SecureSessionsDAO {
	
	/**
	 * 
	 * @param sessionKey
	 * @param sessionModel
	 */
	public abstract void storeSession(@NotNull SessionKey sessionKey, @NotNull SessionModel sessionModel);
	
	
	/**
	 * 
	 * @param sessionKey
	 */
	public abstract void deleteSession(@NotNull SessionKey sessionKey);
	
	
	/**
	 * 
	 * @param sessionKeys
	 */
	public abstract void deleteSessions(@NotNull @Size(min=1, message="One SessionKey Required")  Set<SessionKey> sessionKeys);
	
	
	/**
	 * 
	 * @param sessionKey
	 * @return
	 */
	public abstract int numberOfSessions(@NotNull SessionKey sessionKey);
	
	
	/**
	 * 
	 * @param sessionKey
	 * @return
	 */
	public abstract SessionModel retrieveSession(@NotNull SessionKey sessionKey);

} 