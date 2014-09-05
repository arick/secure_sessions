package co.ssessions.managers;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Session;
import org.apache.catalina.Valve;
import org.apache.catalina.session.PersistentManagerBase;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.juli.logging.Log;

import co.ssessions.conf.EnvConfig;
import co.ssessions.couchbase.CouchbaseSecureSessionsModule;
import co.ssessions.store.SecureSessionsStoreBase;
import co.ssessions.util.SecureSessionsConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SecureSessionsManager extends PersistentManagerBase {
	

	protected static final String name = "SecureSessionsManager";
    protected static final String info = name + "/1.0";
	
    
    protected static Log logger;
    
    /*
     * Values injected from the context.xml file
     */
    protected int maximumInactiveInterval = -1;
    protected int maximumIdleBackup = -1;
    protected String applicationId;
    protected String managerType = SecureSessionsConstants.DEFAULT_MANAGER_TYPE;
    private String configFilePath;
    
    
    private SecureSessionsStoreBase secureSessionStoreBase;
    protected PropertiesConfiguration config;
    
    
	public SecureSessionsManager() {
		this.setSaveOnRestart(true);
		
	}
	

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
	}

	@Override
	protected void initInternal() throws LifecycleException {
		
		/*
		 *  We DO want to enforce this property.  It requires that anything added to the session 
		 *  must be serializable.
		 *  From: Tomcat documentation: ".any user attributes added to a 
		 *        session controlled by this Manager must be Serializable."
		 */
		this.setDistributable(true);
		
		
		/*
		 * Since sessions are discouraged from being stored in memory we do not 
		 * want to reload sessions when the server restarts
		 */
		this.setSaveOnRestart(false);
		
		
		/*
		 * We want the manager to ignore backing up sessions as the SecureSessionsValve will
		 * manually backup the session to the store
		 */
		this.setMaxIdleBackup(-1);
		
		
		/*
		 * We want the manager to not automatically swap out the sessions from memory as 
		 * the valve will manually swap out the sessions from memory after each request.
		 */
		this.setMinIdleSwap(-1);
		this.setMaxIdleSwap(-1);
		
		
		/*
		 * We want an unlimited number of active sessions at any given time.
		 */
		this.setMaxActiveSessions(-1);
		
		
		/*
		 * Sessions do not expire.
		 */
		this.setMaxInactiveInterval(-1);
		
		
		this.logger = this.getContainer().getLogger();
		
		
		if (StringUtils.isBlank(this.configFilePath)) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + " is blank!");
		}
		this.config = EnvConfig.load(this.configFilePath);
		
		
		// Get server configuration from the Manager Configuration Properties
		
		String applicationIdFromConfig = this.config.getString("ss.applicationId");
		if (StringUtils.isBlank(applicationIdFromConfig)) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + "  ss.applicationId is blank!");
		} else {
			this.applicationId = applicationIdFromConfig;
		}
		
		
//		int maxInactiveIntervalFromConfig = this.config.getInt("ss.maxInactiveInterval");
//		if (maxInactiveIntervalFromConfig < 0) {
//			this.setMaxInactiveInterval(this.maximumInactiveInterval); 
//		} else {
//			this.setMaxInactiveInterval(maxInactiveIntervalFromConfig); 
//		}
//		
//		
//		int maxIdleBackupFromConfig = this.config.getInt("ss.maxIdleBackup");
//		if (maxIdleBackupFromConfig < 0) {
//			throw new RuntimeException("In manager properties file located at " + this.configFilePath + "  ss.maxIdleBackup is null!");
//		}
//		this.setMaxIdleBackup(this.maximumIdleBackup);
		
		
		String managerTypeFromConfig = this.config.getString("ss.managerType");
		if (StringUtils.isNotBlank(managerTypeFromConfig)) {
			this.setMaxIdleBackup(this.maximumIdleBackup);
		} 
		
		
		/*
		 * Set up SecureSessionsStoreBase
		 */
		AbstractModule managerTypeModule = this.getManagerTypeModule();
		Injector injector = Guice.createInjector(managerTypeModule);
		
		this.secureSessionStoreBase = injector.getInstance(SecureSessionsStoreBase.class);
		this.secureSessionStoreBase.setApplicationId(this.applicationId);
		this.setStore(this.secureSessionStoreBase);
		
		
		// Add this manager to any SecureSessionsManagagerAware Valves in the pipeline
		
		if ((this.getContainer() != null) 
				&& (this.getContainer().getPipeline() != null)
				&& (this.getContainer().getPipeline().getValves() != null)
				&& (this.getContainer().getPipeline().getValves().length > 0)) {
			
			Pipeline pipeline = this.getContainer().getPipeline();
			
			for(Valve valve : pipeline.getValves()) {
				if (valve instanceof SecureSessionsManagerAware) {
					((SecureSessionsManagerAware) valve).setSecureSessionsManagager(this);
				}
			}
		}
		
		
	}
	
	
	
	public void swapOut(String sessionId) {
		Session session;
		try {
			session = this.findSession(sessionId);
			if (session != null) {
				this.swapOut(session);
			}
		} catch (IOException e) {
			// TODO: Handle exception more gracefully
			e.printStackTrace();
		}
	}
	
	public void loadSession(String sessionId) {
		
		try {
			this.secureSessionStoreBase.load(sessionId);
		} catch (ClassNotFoundException e) {
			// TODO: Handle exception more gracefully
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: Handle exception more gracefully
			e.printStackTrace();
		}
		
	}
	
	public void saveSession(HttpSession httpSession) {
		
		try {
			this.secureSessionStoreBase.save(httpSession);
		} catch (IOException e) {
			// TODO: Handle exception more gracefully
			e.printStackTrace();
		}
	}
	
	
	
	/* **********************************************************************
	 * Getters and Setters
	 * **********************************************************************/
	
	
	
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}


	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}

	
	private AbstractModule getManagerTypeModule() {
		if (this.managerType == "couchbase") {
			return new CouchbaseSecureSessionsModule();
		} else {
			// TODO: Clean up this exception 
			throw new IllegalArgumentException("ManagerType module not found");
		}
	}


	public String getConfigFilePath() {
		return configFilePath;
	}


	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}
	
} // END SecureSessionsManager Class
