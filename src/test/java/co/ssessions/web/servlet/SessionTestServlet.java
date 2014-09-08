package co.ssessions.web.servlet;


/**
 * 
 */
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
public class SessionTestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
 
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession(false);
		httpSession.setAttribute("Tester", "Great!");
		
		
		
		response.setHeader("Host", "localhost");
		
		
		
		StringBuffer buff = new StringBuffer();
		Enumeration<String> attributeNames = httpSession.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attributeValue = httpSession.getAttribute(attributeName);
			buff.append(attributeName);
			buff.append(": ");
			buff.append(attributeValue);
			buff.append("\n");
		}
		response.getWriter().append("Session Values:\n" + buff.toString() + "\n\n");
		
		
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) { 
			response.getWriter().append("Cookie Length: " + cookies.length + "\n\n");
			for (int i = 0; i < cookies.length; ++i) {
				Cookie cookie = cookies[i];
				response.getWriter().append("Cookie " + i + ": " + cookie.getDomain() + " ~ " + cookie.getName() + " ~ " + cookie.getValue() + " ~~~ " + cookie.toString() + "\n\n");
			}
		}
		
		response.getWriter()
				.append(String.format("It's %s now\n\n\n\nwww.hascode.com",
						new Date()));
		
		response.getWriter().flush();
		
	}
}