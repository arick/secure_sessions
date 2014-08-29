package co.ssessions.managers;

import java.io.File;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.session.PersistentManagerBase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.juli.logging.Log;

import co.ssessions.conf.EnvironmentConfiguration;
import co.ssessions.modules.CouchbaseSecureSessionsModule;
import co.ssessions.store.SecureSessionsStoreBase;
import co.ssessions.util.SecureSessionsConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SecureSessionManagager extends PersistentManagerBase {
	

	protected static final String name = "SecureSessionManagager";
    protected static final String info = name + "/1.0";
	
    
    protected static Log logger;
    
    /*
     * Values injected from the context.xml file
     */
    protected int maximumInactiveInterval; //2 hours
    protected int maximumIdleBackup; // 30 seconds
    protected String applicationId;
    protected String managerType = SecureSessionsConstants.DEFAULT_MANAGER_TYPE;
    private String configFilePath;
    
    
    private SecureSessionsStoreBase secureSessionStoreBase;
    protected PropertiesConfiguration config;
    
    
	public SecureSessionManagager() {
		
		if (StringUtils.isBlank(this.configFilePath)) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + " is blank!");
		}
		EnvironmentConfiguration.load(this.configFilePath);
		
		
		this.setSaveOnRestart(true);
		
		
		// Get server configuration from the Manager Configuration Properties
		
		this.applicationId = this.config.getString("ss.applicationId");
		if (StringUtils.isBlank(this.applicationId)) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + "  ss.applicationId is blank!");
		}
		
		
		int maxInactiveInterval = this.config.getInt("ss.maxInactiveInterval");
		if (maxInactiveInterval < 0) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + "  ss.maxInactiveInterval is null!");
		}
		this.setMaxInactiveInterval(maxInactiveInterval); 
		
		
		int maxIdleBackup = this.config.getInt("ss.maxIdleBackup");
		if (maxIdleBackup < 0) {
			throw new RuntimeException("In manager properties file located at " + this.configFilePath + "  ss.maxIdleBackup is null!");
		}
		this.setMaxIdleBackup(this.maximumIdleBackup);
		
	}
	

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
	}

	@Override
	protected void initInternal() throws LifecycleException {
		
		this.setDistributable(true);
		
		this.logger = this.getContainer().getLogger();
		
		
		AbstractModule managerTypeModule = this.getManagerTypeModule();
		Injector injector = Guice.createInjector(managerTypeModule);
		
		
		/*
		 * Set up SecureSessionsStoreBase
		 */
		this.secureSessionStoreBase = injector.getInstance(SecureSessionsStoreBase.class);
		this.secureSessionStoreBase.setApplicationId(this.applicationId);
		this.setStore(this.secureSessionStoreBase);
		
	}

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
	
} // END SecureSessionManagager Class
