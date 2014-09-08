package co.ssessions.web;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer implements Runnable {

	public Tomcat tomcat;
	private String url;
	
	public TomcatServer(Tomcat tomcat) {
		this.tomcat = tomcat;
	}
	
	@Override
	public void run() {
		
		
		try {
			this.tomcat.start();
			this.tomcat.getServer().await();
			
			
		} catch (LifecycleException e) {
			// TODO Handle exception more gracefully
			e.printStackTrace();
		}	
	}
	
	
	public void stop() {
		try {
			this.tomcat.stop();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String getUrl() {
		return url;
	}
	
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
