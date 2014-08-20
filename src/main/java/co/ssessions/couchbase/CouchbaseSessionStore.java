package co.ssessions.couchbase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.catalina.Container;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.catalina.util.CustomObjectInputStream;

import co.ssessions.SessionKey;
import co.ssessions.SessionModel;
import co.ssessions.crypto.CryptoService;

import com.couchbase.client.CouchbaseClient;

public class CouchbaseSessionStore extends StoreBase {
	
	private static final String name = "CouchbaseSessionStore";
    private static final String info = name + "/1.0";
	
    private Set<String> keys = Collections.synchronizedSet(new HashSet<String>());
	
	protected CouchbaseClient couchbaseClient;
	protected String applicationId;
	protected Map<String, String> privateKeyMap;
	
	protected CryptoService cryptoService;
	
	
	public CouchbaseSessionStore() {
	}

	@Override
	public int getSize() throws IOException {
		
		SessionKey sessionKey = new SessionKey();
		sessionKey.setApplicationId(this.applicationId);
		
		int size = CouchbaseDB.numberOfSessions(this.couchbaseClient, sessionKey);
		return size;
	}

	
	@Override
	public String[] keys() throws IOException {
		return keys.toArray(new String[0]);
	}

	
	@Override
	public Session load(String id) throws ClassNotFoundException, IOException {
		
		
		SessionKey sessionKey = new SessionKey(this.applicationId, id);
		SessionModel sessionModel = CouchbaseDB.retrieveSession(this.couchbaseClient, sessionKey);
		
		String encryptedSessionDataBase64String = (String) sessionModel.getData();
		byte[] encryptedSessionDataBytes = Base64.getDecoder().decode(encryptedSessionDataBase64String);
		
		
		/*
		 * Decrypt Session Data
		 */
		byte[] decryptedSessionDataBytes = this.cryptoService.decrypt(encryptedSessionDataBytes);
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedSessionDataBytes);
		
		Object httpSessionMapObject = null;
		CustomObjectInputStream customObjectInputStream = null;
		
		Container container = this.getManager().getContainer();
		customObjectInputStream = new CustomObjectInputStream(byteArrayInputStream, container.getLoader().getClassLoader());
		httpSessionMapObject = customObjectInputStream.readObject();
		customObjectInputStream.close();
		
		Session session = this.getManager().createSession(id);
		
		if (httpSessionMapObject instanceof Map<?, ?>) {
            Map<String, Object> sessionAttributeMap = (Map<String, Object>) httpSessionMapObject;

            for (String attributeKey : sessionAttributeMap.keySet()) {
                ((StandardSession)session).setAttribute(attributeKey, sessionAttributeMap.get(attributeKey));
            }
        } else {
            throw new RuntimeException("Error: Unable to unmarshall session attributes from Couchbase database");
        }
		
		this.manager.add(session);
        this.keys.add(sessionKey.toString());
		
		return session;
	}

	
	@Override
	public void remove(String id) throws IOException {
		
		SessionKey sessionKey = new SessionKey(this.applicationId, id);
		CouchbaseDB.deleteSession(this.couchbaseClient, sessionKey);
        this.keys.remove(sessionKey.toString());
	}

	
	@Override
	public void clear() throws IOException {
		
		Set<SessionKey> sessionKeys = new HashSet<SessionKey>();
		for (String key: this.keys) {
			sessionKeys.add(new SessionKey().fromString(key));
		}

		CouchbaseDB.deleteSessions(this.couchbaseClient, sessionKeys);
		this.keys.clear();
	}

	
	@Override
	public void save(Session session) throws IOException {
		
		HttpSession httpSession = session.getSession();
		
		SessionKey sessionKey = new SessionKey(this.applicationId, httpSession.getId());
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		
		Map<String, Object> sessionDataMap = new HashMap<String, Object>();
		Enumeration<String> sessionNames = httpSession.getAttributeNames();
		
		while (sessionNames.hasMoreElements()) {
			
			String attributeName = sessionNames.nextElement();
			Object attributeValue = httpSession.getAttribute(attributeName);
			sessionDataMap.put(attributeName, attributeValue);
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(sessionDataMap);
		objectOutputStream.close();
		
		byte[] sessionDataBytes = byteArrayOutputStream.toByteArray();
		
		
		// Let's Encrypt the sessionBytes
		byte[] encryptedSessionDataBytes = this.cryptoService.encrypt(sessionDataBytes);
		
		
		// Base64 Encode the sessionData
		String encryptedSessionDataBytesBase64 = Base64.getEncoder().encodeToString(encryptedSessionDataBytes);
		sessionModel.setData(encryptedSessionDataBytesBase64);
		
		
		CouchbaseDB.storeSession(this.couchbaseClient, sessionKey, sessionModel);
		
        this.keys.add(sessionKey.toString());
	}
	
	
	public void setCouchbaseClient(CouchbaseClient couchbaseClient) {
		this.couchbaseClient = couchbaseClient;
	}
	
	
	@Override
	public String getStoreName() {
		return name;
	}

	
	@Override
	public String getInfo() {
		return info;
	}

	
	public String getApplicationId() {
		return applicationId;
	}

	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	
	public Map getPrivateKeyMap() {
		return privateKeyMap;
	}

	
	public void setPrivateKeyMap(Map privateKeyMap) {
		this.privateKeyMap = privateKeyMap;
	}

	public CryptoService getCryptoService() {
		return cryptoService;
	}

	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}

} // END CouchbaseSessionStore Class
