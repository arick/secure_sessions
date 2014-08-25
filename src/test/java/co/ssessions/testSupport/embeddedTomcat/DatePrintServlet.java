package co.ssessions.testSupport.embeddedTomcat;


/**
 * 
 */
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
public class DatePrintServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
 
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		
		HttpSession httpSession = req.getSession();
		httpSession.setAttribute("Tester", "Great!");
		
		res.getWriter()
				.append(String.format("It's %s now\n\n\n\nwww.hascode.com",
						new Date()));
	}
}