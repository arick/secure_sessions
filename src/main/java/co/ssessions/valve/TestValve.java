package co.ssessions.valve;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class TestValve extends ValveBase {

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {

		System.out.println("Before Test Valve Request: " + request.toString());
		System.out.println("Before Test Valve Response: " + response.toString());
		
		this.getNext().invoke(request, response);
		
		System.out.println("After Test Valve Request: " + request.toString());
		System.out.println("After Test Valve Response: " + response.toString());
		
	}

}
