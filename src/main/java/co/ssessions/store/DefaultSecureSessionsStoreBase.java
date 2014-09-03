package co.ssessions.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
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
import org.apache.commons.lang3.StringUtils;

import co.ssessions.crypto.CryptoService;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.models.SessionKey;
import co.ssessions.models.SessionModel;
import co.ssessions.util.SecureSessionsConstants;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Inject;

public class DefaultSecureSessionsStoreBase extends SecureSessionsStoreBase {
	
	private static final String name = "DefaultSecureSessionsStoreBase";
	private static final String info = name + "/1.0";
	
	private Set<String> keys = Collections.synchronizedSet(new HashSet<String>());
	
	protected Map<String, String> privateKeyMap;
	
	protected CryptoService cryptoService;
	private SecureSessionsDAO secureSessionsDao;
	
	
	@Inject
	public DefaultSecureSessionsStoreBase(SecureSessionsDAO secureSessionsDao, CryptoService cryptoService) {
		this.cryptoService = cryptoService;
		this.secureSessionsDao = secureSessionsDao;
	}
	
	
	@Override
	public int getSize() throws IOException {
		
		SessionKey sessionKey = new SessionKey();
		sessionKey.setApplicationId(this.applicationId);
		
		int size = this.secureSessionsDao.numberOfSessions(sessionKey);
		return size;
	}

	
	@Override
	public String[] keys() throws IOException {
		return this.keys.toArray(new String[0]);
	}

	
	@Override
	public Session load(String id) throws ClassNotFoundException, IOException {
		
		SessionKey sessionKey = new SessionKey(this.applicationId, id);
		SessionModel sessionModel = this.secureSessionsDao.retrieveSession(sessionKey);
		
		StandardSession session = (StandardSession) this.getManager().createSession(id);
		
		// If there is not a SessionModel for this user then do not try to unpack it...
		if (sessionModel != null) {
			
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
			
			if (httpSessionMapObject instanceof Map<?, ?>) {
				Map<String, Object> sessionAttributeMap = (Map<String, Object>) httpSessionMapObject;
				
				for (String attributeKey : sessionAttributeMap.keySet()) {
					session.setAttribute(attributeKey, sessionAttributeMap.get(attributeKey));
				}
				
			} else {
				throw new RuntimeException("Error: Unable to unmarshall session attributes from Couchbase database");
			}
		
		} // END if (sessionModel != null) Method
		
		Date timeStampDate = new Date();
		session.setAttribute(SecureSessionsConstants.CREATE_TIME_SESSION_MAP_KEY, (sessionModel != null) ? sessionModel.getCreateTime().getTime() : timeStampDate.getTime());
		session.setAttribute(SecureSessionsConstants.UPDATE_TIME_SESSION_MAP_KEY, (sessionModel != null) ? sessionModel.getUpdateTime().getTime() : timeStampDate.getTime());
		
		
		this.manager.add(session);
		this.keys.add(sessionKey.getSessionId());
		
		return session;
	}

	
	@Override
	public void remove(String id) throws IOException {
		
		SessionKey sessionKey = new SessionKey(this.applicationId, id);
		this.secureSessionsDao.deleteSession(sessionKey);
		this.keys.remove(sessionKey.getSessionId());
	}

	
	@Override
	public void clear() throws IOException {
		
		Set<SessionKey> sessionKeys = new HashSet<SessionKey>();
		for (String key: this.keys) {
			sessionKeys.add(new SessionKey(this.applicationId, key));
		}

		this.secureSessionsDao.deleteSessions(sessionKeys);
		this.keys.clear();
	}

	@Override
	public void save(Session session) throws IOException {
		
		HttpSession httpSession = session.getSession();
		this.save(httpSession);
	}
	
	
	public void save(HttpSession httpSession) throws IOException {
		
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

		Date dateNow = new Date();
		sessionModel.setUpdateTime(dateNow);
		if (sessionDataMap.get(SecureSessionsConstants.CREATE_TIME_SESSION_MAP_KEY) == null) {
			sessionModel.setCreateTime(dateNow);
		} else {
			sessionModel.setCreateTime(new Date((long) sessionDataMap.get(SecureSessionsConstants.CREATE_TIME_SESSION_MAP_KEY)));
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
		
		
		this.secureSessionsDao.storeSession(sessionKey, sessionModel);
		
		this.keys.add(sessionKey.getSessionId());
	}
	
	
	@Override
	public String getStoreName() {
		return name;
	}

	
	@Override
	public String getInfo() {
		return info;
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

} // END DefaultSecureSessionsStoreBase Class
