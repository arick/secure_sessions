package co.ssessions.valve;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import co.ssessions.managers.SecureSessionsManager;
import co.ssessions.managers.SecureSessionsManagerAware;

public class SecureSessionsValve extends ValveBase implements SecureSessionsManagerAware {

	SecureSessionsManager secureSessionsManager;

	@Override
	public void invoke(Request request, Response response) throws IOException,ServletException {
		
		if (this.secureSessionsManager == null) {
			throw new RuntimeException("In SecureSessionsValve.invoke(request, response) SecureSessionsManager is not initialized");
		}
		
		String sessionId = null;
		HttpSession httpSession = null;
		/*
		 * This bit of code is executed while the request is coming INTO the Container
		 */
		httpSession = request.getSession(true);
		
		if (httpSession != null) {
			sessionId = httpSession.getId();
			this.secureSessionsManager.loadSession(sessionId);
		}
		
		/*
		 * Invoke the next valve in the pipeline
		 */
		this.getNext().invoke(request, response);
		
		
		/*
		 * This bit of code is executed while the request is leaving the Container
		 */
		httpSession = request.getSession(false);
		if (httpSession != null) {
			sessionId = httpSession.getId();
			this.secureSessionsManager.swapOut(sessionId);
		}
		
	}

	@Override
	public void setSecureSessionsManagager(SecureSessionsManager secureSessionsManager) {
		this.secureSessionsManager = secureSessionsManager;
	}

}
