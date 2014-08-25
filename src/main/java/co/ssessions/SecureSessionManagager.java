package co.ssessions;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.session.PersistentManagerBase;
import org.apache.juli.logging.Log;

import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.PKCS8CryptoService;

public class SecureSessionManagager extends PersistentManagerBase {
	

	protected static final String name = "SecureSessionManagager";
    protected static final String info = name + "/1.0";
	protected CryptoService cryptoService;
    
    protected static Log logger;
    
    /*
     * Values injected from the context.xml file
     */
    protected String privateKeyFilePath;
    protected int maximumInactiveInterval = 60 * 60 * 2; //2 hours
    protected int maximumIdleBackup = 30; // 30 seconds
    protected String applicationId = null;
    
    
	public SecureSessionManagager() {
		
		this.setSaveOnRestart(true);
		this.setMaxInactiveInterval(this.maximumInactiveInterval); 
		this.setMaxIdleBackup(this.maximumIdleBackup);
	
		
		// Setup the chosen Store Base
		
		
		
	}
	

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
	}

	@Override
	protected void initInternal() throws LifecycleException {

		this.setDistributable(true);
		
		this.logger = this.getContainer().getLogger();
		
		
		// Set up Security PKI Certs
		this.cryptoService = new PKCS8CryptoService(this.privateKeyFilePath);
		
		// Set up SessionStore?
		
		
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}


	/**
	 * Absolute path including the file name of the PKCS8 formated private key.
	 * 
	 * @param privateKeyFilePath
	 */
	public void setPrivateKeyFilePath(String privateKeyFilePath) {
		this.privateKeyFilePath = privateKeyFilePath;
	}


	/**
	 * Set the default maximum inactive interval (in seconds) for Sessions created by this Manager.
     * Default is 2 hours
     * 
	 * @param maximumInactiveInterval
	 */
	public void setMaximumInactiveInterval(int maximumInactiveInterval) {
		this.maximumInactiveInterval = maximumInactiveInterval;
	}


	/**
	 * Sets the option to back sessions up to the Store after they are used in a request. Sessions remain available in memory 
     * after being backed up, so they are not passivated as they are when swapped out. The value set indicates how old a 
     * session may get (since its last use) before it must be backed up: -1 means sessions are not backed up. <br/>
     * <br/>
     * Default is 30 seconds
	 * 
	 * @param maximumIdleBackup
	 */
	public void setMaximumIdleBackup(int maximumIdleBackup) {
		this.maximumIdleBackup = maximumIdleBackup;
	}


	public CryptoService getCryptoService() {
		return cryptoService;
	}


	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}


	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	
	
	
	
} // END SecureSessionManagager Class
