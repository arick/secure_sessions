package co.ssessions.couchbase;

import java.util.Date;
import java.util.Set;

import net.spy.memcached.PersistTo;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;
import co.ssessions.models.SessionKey;
import co.ssessions.models.SessionModel;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public class CouchbaseDAO implements SecureSessionsDAO {
	
	public static final String VIEW_NAME = "bykeyname";
	public static final String DESIGN_DOC_NAME = "dev_bykey";
	
	private CouchbaseClient couchbaseClient;
	
	@Inject
	public CouchbaseDAO(CouchbaseClientHolder couchbaseClienthHolder) {
		this.couchbaseClient = couchbaseClienthHolder.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see co.ssessions.couchbase.SecureSessionsDAO#storeSession(com.couchbase.client.CouchbaseClient, co.ssessions.models.SessionKey, co.ssessions.models.SessionModel)
	 */
	@Override
	public void storeSession(SessionKey sessionKey, SessionModel sessionModel) {

		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		String sessionKeyString = sessionKey.toString();
		this.couchbaseClient.set(sessionKeyString, sessionModelJson, PersistTo.ONE);
	}


	/* (non-Javadoc)
	 * @see co.ssessions.couchbase.SecureSessionsDAO#deleteSession(com.couchbase.client.CouchbaseClient, co.ssessions.models.SessionKey)
	 */
	@Override
	public void deleteSession(SessionKey sessionKey) {

		this.couchbaseClient.delete(sessionKey.toString(), PersistTo.ONE);
	}


	/* (non-Javadoc)
	 * @see co.ssessions.couchbase.SecureSessionsDAO#deleteSessions(com.couchbase.client.CouchbaseClient, java.util.Set)
	 */
	@Override
	public void deleteSessions(Set<SessionKey> sessionKeys) {

		for (SessionKey sessionKey : sessionKeys) {
			this.deleteSession(sessionKey);
		}
	}


	/* (non-Javadoc)
	 * @see co.ssessions.couchbase.SecureSessionsDAO#numberOfSessions(com.couchbase.client.CouchbaseClient, co.ssessions.models.SessionKey)
	 */
	@Override
	public int numberOfSessions(SessionKey sessionKey) {
		
		View view = this.couchbaseClient.getView(CouchbaseDAO.DESIGN_DOC_NAME, CouchbaseDAO.VIEW_NAME);
		Query query = new Query();
		query.setStale(Stale.FALSE);
		query.setIncludeDocs(true); 
		query.setRangeStart(sessionKey.getApplicationId() + sessionKey.getDelimiter()); 
		query.setRangeEnd(sessionKey.getApplicationId() + sessionKey.getDelimiter() + "\uefff");

		
		ViewResponse response = this.couchbaseClient.query(view, query);
		int numberOfSessions = response.size();
		return numberOfSessions;
	}	
	
	
	/* (non-Javadoc)
	 * @see co.ssessions.couchbase.SecureSessionsDAO#retrieveSession(com.couchbase.client.CouchbaseClient, co.ssessions.models.SessionKey)
	 */
	@Override
	public SessionModel retrieveSession(SessionKey sessionKey) {

		String sessionModelJson = (String) this.couchbaseClient.get(sessionKey.toString());
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonDeserializer()).create();
		SessionModel sessionModel = (SessionModel) gson.fromJson(sessionModelJson, SessionModel.class); 
		
		return sessionModel;
		
	}
	

} // END CouchbaseDAO Class
