package co.ssessions.valve;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import co.ssessions.managers.SecureSessionsManagager;
import co.ssessions.managers.SecureSessionsManagerAware;

public class SecureSessionsValve extends ValveBase implements SecureSessionsManagerAware {

	SecureSessionsManagager secureSessionsManager;

	@Override
	public void invoke(Request request, Response response) throws IOException,ServletException {
		
		if (this.secureSessionsManager == null) {
			throw new RuntimeException("In SecureSessionsValve.invoke(request, response) SecureSessionsManagager is not initialized");
		}
		
		/*
		 * This bit of code is executed while the request is coming INTO the Container
		 */
		HttpSession httpSession = request.getSession(false);
		String sessionId = httpSession.getId();
		
		this.secureSessionsManager.loadSession(sessionId);
		
		
		/*
		 * Invoke the next valve in the pipeline
		 */
		this.getNext().invoke(request, response);
		
		
		/*
		 * This bit of code is executed while the request is leaving the Container
		 */
		if (httpSession != null) {
			this.secureSessionsManager.saveSession(httpSession);
		}
		
	}

	@Override
	public void setSecureSessionsManagager(SecureSessionsManagager secureSessionsManagager) {
		this.secureSessionsManager = secureSessionsManagager;
	}

}
