package co.ssessions.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadBalancer {
	
	List<Map<String,String>> serverConfigs;
	
	int serverIndex = -1;
	
	
	
	public LoadBalancer() {
		this.serverConfigs = new ArrayList<Map<String,String>>();
	}
	
	public void addServerConfig(Map<String,String> serverConfig) {
		
		this.serverConfigs.add(serverConfig);
		
	}
	
	
	public int getMaxServerIndex() {
		return this.serverConfigs.size() - 1;
	}
	
	
	public String getNextServerUrl() {
		int tempIndex = this.serverIndex;
		
		if (this.serverIndex >= this.getMaxServerIndex()) {
			this.serverIndex = 0;
		} else {
			this.serverIndex++;
		}
		
		return this.serverConfigs.get(tempIndex).get("URL");
	}
	
	
}
