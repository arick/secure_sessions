package co.ssessions.couchbase;

import java.util.Date;
import java.util.Set;

import net.spy.memcached.PersistTo;
import co.ssessions.SessionKey;
import co.ssessions.SessionModel;
import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CouchbaseDB {
	
	public static final String VIEW_NAME = "bykeyname";
	public static final String DESIGN_DOC_NAME = "dev_bykey";
	
	public static void storeSession(CouchbaseClient couchbasecClient, SessionKey sessionKey, SessionModel sessionModel) {

		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		String sessionKeyString = sessionKey.toString();
		couchbasecClient.set(sessionKeyString, sessionModelJson, PersistTo.ONE);
	}


	public static void deleteSession(CouchbaseClient couchbaseClient, SessionKey sessionKey) {

		couchbaseClient.delete(sessionKey.toString(), PersistTo.ONE);
	}


	public static void deleteSessions(CouchbaseClient couchbaseClient, Set<SessionKey> sessionKeys) {

		for (SessionKey sessionKey : sessionKeys) {
			CouchbaseDB.deleteSession(couchbaseClient, sessionKey);
		}
	}


	public static int numberOfSessions(CouchbaseClient couchbaseClient, SessionKey sessionKey) {
		
		View view = couchbaseClient.getView(CouchbaseDB.DESIGN_DOC_NAME, CouchbaseDB.VIEW_NAME);
		Query query = new Query();
		query.setStale(Stale.FALSE);
		query.setIncludeDocs(true); 
		query.setRangeStart(sessionKey.getApplicationId() + sessionKey.getDelimiter()); 
		query.setRangeEnd(sessionKey.getApplicationId() + sessionKey.getDelimiter() + "\uefff");

		
		ViewResponse response = couchbaseClient.query(view, query);
		int numberOfSessions = response.size();
		return numberOfSessions;
	}	
	
	
	public static SessionModel retrieveSession(CouchbaseClient couchbaseClient, SessionKey sessionKey) {

		String sessionModelJson = (String) couchbaseClient.get(sessionKey.toString());
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonDeserializer()).create();
		SessionModel sessionModel = (SessionModel) gson.fromJson(sessionModelJson, SessionModel.class); 
		
		return sessionModel;
		
	}
	

} // END CouchbaseDB Class
