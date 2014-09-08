package co.ssessions.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.catalina.Context;
import org.apache.catalina.Pipeline;
import org.apache.catalina.startup.Tomcat;

import co.ssessions.conf.EnvConfig;
import co.ssessions.managers.SecureSessionsManager;
import co.ssessions.valve.SecureSessionsValve;

public class TomcatServerBuilder {

	private int portNumber;
	
	private String baseFolder;
	
	private String applicationContext = "/";
	
	private SecureSessionsManager secureSessionManagger;
	
	private SecureSessionsValve secureSessionsValve;
	
	private String servletName = "app";
	
	private HttpServlet httpServlet;
	
	private String urlPattern = "/a";
	
	
	public TomcatServer buildInstance() {
		
		if (!this.isValid()) {
			throw new RuntimeException("ERROR: TomcatServerBuilder is not in the appropriate state to build a functional TomcatServer instance!");
		}
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(this.portNumber);

		File base = new File(this.baseFolder);

		Context rootCtx = tomcat.addContext(this.applicationContext, base.getAbsolutePath());
		
		Pipeline pipe = rootCtx.getPipeline();
		pipe.addValve(this.secureSessionsValve);
		
		this.secureSessionManagger.setConfigFilePath(EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath"));

		rootCtx.setManager(this.secureSessionManagger);

		Tomcat.addServlet(rootCtx, this.servletName, this.httpServlet);

		rootCtx.addServletMapping(this.urlPattern, this.servletName);
		
		TomcatServer tomcatServer = new TomcatServer(tomcat);
		
		String url = "http://localhost:" 
						+ this.portNumber
						+ this.applicationContext
						+ this.urlPattern.substring(1);
		
		tomcatServer.setUrl(url);
		
		return tomcatServer;
		
	}
	
	
	public Map<String, String> startNewTomcatApplication() {
		
		Map<String, String> outputs = new HashMap<String, String>();
		
		String url = "http://localhost:" 
					+ this.portNumber
					+ this.applicationContext
					+ this.urlPattern.substring(1);
		outputs.put("URL", url);
		
		
		String[] classpathEntries = {
				"bin",
				"src/test/resources/tomcat_server_libs/aopalliance-1.0.jar",
				"src/test/resources/tomcat_server_libs/classmate-1.0.0.jar",
				"src/test/resources/tomcat_server_libs/commons-codec-1.6.jar",
				"src/test/resources/tomcat_server_libs/commons-configuration-1.10.jar",
				"src/test/resources/tomcat_server_libs/commons-lang3-3.3.2.jar",
				"src/test/resources/tomcat_server_libs/commons-logging-1.1.3.jar",
				"src/test/resources/tomcat_server_libs/couchbase-client-1.4.4.jar",
				"src/test/resources/tomcat_server_libs/gson-2.3.jar",
				"src/test/resources/tomcat_server_libs/guava-17.0.jar",
				"src/test/resources/tomcat_server_libs/guice-3.0.jar",
				"src/test/resources/tomcat_server_libs/hamcrest-all-1.3.jar",
				"src/test/resources/tomcat_server_libs/hibernate-validator-5.1.2.Final.jar",
				"src/test/resources/tomcat_server_libs/httpclient-4.3.5.jar",
				"src/test/resources/tomcat_server_libs/httpcore-4.3.2.jar",
				"src/test/resources/tomcat_server_libs/httpcore-nio-4.3.jar",
				"src/test/resources/tomcat_server_libs/javax.inject-1.jar",
				"src/test/resources/tomcat_server_libs/jboss-logging-3.1.3.GA.jar",
				"src/test/resources/tomcat_server_libs/jettison-1.1.jar",
				"src/test/resources/tomcat_server_libs/junit-4.11.jar",
				"src/test/resources/tomcat_server_libs/log4j-api-2.0.1.jar",
				"src/test/resources/tomcat_server_libs/log4j-core-2.0.1.jar",
				"src/test/resources/tomcat_server_libs/mockito-all-1.9.5.jar",
				"src/test/resources/tomcat_server_libs/netty-3.5.5.Final.jar",
				"src/test/resources/tomcat_server_libs/spymemcached-2.11.4.jar",
				"src/test/resources/tomcat_server_libs/stax-api-1.0.1.jar",
				"src/test/resources/tomcat_server_libs/tomcat-annotations-api-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/tomcat-api-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/tomcat-catalina-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/tomcat-embed-core-7.0.54.jar",
				"src/test/resources/tomcat_server_libs/tomcat-juli-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/tomcat-servlet-api-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/tomcat-util-7.0.42.jar",
				"src/test/resources/tomcat_server_libs/validation-api-1.1.0.Final.jar"
		};
		
		String classpath = String.join(":", classpathEntries);
		String execString = "java -classpath " + classpath + " co.ssessions.web.TomcatServerApplication " + this.portNumber;
		
		System.out.println("exec -->" + execString);
		
		
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(execString);
			
			InputStream is = process.getInputStream();
			InputStream errorStream = process.getErrorStream();
			
			Reader errorReader = new InputStreamReader(errorStream);
			BufferedReader errorBufferedReader = new BufferedReader(errorReader);
			
			String errorLine = errorBufferedReader.readLine();
			while (errorLine != null) {
				
				System.out.println(errorLine);
				errorLine = errorBufferedReader.readLine();
			}	
			
			
			Reader r = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(r);
			
			String line = br.readLine();
			while (line != null) {
				
				System.out.println(line);
				line = br.readLine();
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		return outputs;
	}

	public boolean isValid() {
		return true;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}



	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}



	public void setApplicationContext(String applicationContext) {
		this.applicationContext = applicationContext;
	}



	public void setSecureSessionManagger(SecureSessionsManager secureSessionManagger) {
		this.secureSessionManagger = secureSessionManagger;
	}



	public void setSecureSessionsValve(SecureSessionsValve secureSessionsValve) {
		this.secureSessionsValve = secureSessionsValve;
	}



	public void setServletName(String servletName) {
		this.servletName = servletName;
	}



	public void setHttpServlet(HttpServlet httpServlet) {
		this.httpServlet = httpServlet;
	}
	
	
}
