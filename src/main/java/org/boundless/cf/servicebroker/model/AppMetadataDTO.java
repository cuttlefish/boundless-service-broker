package org.boundless.cf.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/*
 * A Data transfer object to move data around
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class AppMetadataDTO {

	private static transient final Logger log = Logger.getLogger(AppMetadataDTO.class);

	@JsonSerialize
	@JsonProperty("org")
	private String org;
	
	@JsonSerialize
	@JsonProperty("space")
	private String space;

	@JsonSerialize
	@JsonProperty("org_id")
	private String orgGuid;

	@JsonSerialize
	@JsonProperty("space_id")
	private String spaceGuid;

	@JsonSerialize
	@JsonProperty("domain")
	private String domain;	
	
	@JsonSerialize
	@JsonProperty("domain_id")
	private String domainGuid;
	
	@JsonSerialize
	@JsonProperty("app_id")
	private String appGuid;
	
	@JsonSerialize
	@JsonProperty("type")
	private String type;
	
	@JsonSerialize
	@JsonProperty("name")
	private String name;
	
	@JsonSerialize
	@JsonProperty("uri")
	private String uri;
	
	@JsonSerialize
	@JsonProperty("route_id")
	private String routeGuid;
	
	@JsonSerialize
	@JsonProperty("docker_image")
	private String dockerImage;
	
	@JsonSerialize
	@JsonProperty("memory")
	private int memory = 1024;
	
	@JsonSerialize
	@JsonProperty("disk")
	private int disk = 2048;
	
	@JsonSerialize
	@JsonProperty("instances")
	private int instances = 1;
	
	@JsonSerialize
	@JsonProperty("command")
	private String startCommand;
	
	@JsonSerialize
	@JsonProperty("state")	
	private String state;
	
	@JsonSerialize
	@JsonProperty("dockerCred")
	protected Map<String,String> dockerCred = new HashMap<String,String>();
	
	@JsonSerialize
	@JsonProperty("environment_jsons")
	private Map<String, Object> environmentJsons = new HashMap<String,Object>();
	
	@SuppressWarnings("unchecked")
	public void setMapping(String key, Object val) {
		switch(key) {
			case "org": this.setOrg(val.toString()); break;
			case "dockerCred": this.setDockerCred((HashMap<String, String>) val); break;
			case "environment": this.setEnvironmentJsons( (HashMap<String, Object>) val); break;
			case "domain": this.setDomain(val.toString()); break;
			case "space": this.setSpace(val.toString()); break;
			case "type": this.setType(val.toString()); break;
			case "name": this.setName(val.toString()); break;
			case "route": 
			case "uri" : 
				this.setRoute(val.toString()); break;
			case "instances": this.setInstances(Integer.parseInt(val.toString())); break;
			case "memory": this.setMemory(Integer.parseInt(val.toString())); break;
			case "disk": this.setDisk(Integer.parseInt(val.toString())); break;
			case "docker_image": this.setDockerImage(val.toString()); break;
		default:
			log.info("Could not map parameter: " + key + " with value: " + val);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isValid() {
		return this.dockerImage != null;
	}
	
	public String getOrgGuid() {
		return orgGuid;
	}

	public void setOrgGuid(String orgGuid) {
		this.orgGuid = orgGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public String getDomainGuid() {
		return domainGuid;
	}

	public void setDomainGuid(String domainGuid) {
		this.domainGuid = domainGuid;
	}

	public Map<String, String> getDockerCred() {
		return dockerCred;
	}

	public void setDockerCred(Map<String, String> dockerCred) {
		this.dockerCred = dockerCred;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	public String getRouteGuid() {
		return routeGuid;
	}

	public void setRouteGuid(String routeGuid) {
		this.routeGuid = routeGuid;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> convertToMap(String content) {
		HashMap<String, String> content_map = null;
		try {
			content_map = (HashMap<String, String>) (new JSONParser().parse(content));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content_map;
	}	

	@SuppressWarnings("unchecked")
	public Map<String, String> convertToMap(JSONObject content) {
		HashMap<String, String> content_map = (HashMap<String, String>) content;
		return content_map;
	}
	
	public Map<String, Object> getEnvironmentJsons() {
		return environmentJsons;
	}

	public void setEnvironmentJsons(Map<String, Object> environmentJsons) {
		this.environmentJsons = environmentJsons;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRoute() {
		return uri;
	}
	
	public void setRoute(String routeName) {
		this.uri = routeName;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}
	
	public String getSpace() {
		return space;
	}
	
	public void setSpace(String space) {
		this.space = space;
	}
	
	public String getDockerImage() {
		return dockerImage;
	}
	
	public void setDockerImage(String dockerImage) {
		this.dockerImage = dockerImage;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((dockerImage == null) ? 0 : dockerImage.hashCode());
		result = prime * result
				+ ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((org == null) ? 0 : org.hashCode());
		result = prime * result + ((orgGuid == null) ? 0 : orgGuid.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result
				+ ((routeGuid == null) ? 0 : routeGuid.hashCode());
		result = prime * result + ((space == null) ? 0 : space.hashCode());
		result = prime * result
				+ ((spaceGuid == null) ? 0 : spaceGuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppMetadataDTO other = (AppMetadataDTO) obj;
		if ((name == null) && (other.name != null)) {
				return false;
		} 
		
		return name.equals(other.name);
	}	

	public int getMemory() {
		return memory;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public int getDisk() {
		return disk;
	}
	
	public void setDisk(int disk) {
		this.disk = disk;
	}
	
	public int getInstances() {
		return instances;
	}
	public void setInstances(int instances) {
		this.instances = instances;
	}
	
	public String getStartCommand() {
		return startCommand;
	}
	
	public void setStartCommand(String startCommand) {
		this.startCommand = startCommand;
	}
	
	@Override
	public String toString() {
		return "AppMetadata [org=" + org + ", space=" + space + ", orgGuid="
				+ orgGuid + ", spaceGuid=" + spaceGuid + ", domain=" + domain
				+ ", domainGuid=" + domainGuid + ", appGuid=" + appGuid
				+ ", type=" + type + ", name=" + name + ", uri=" + uri
				+ ", routeGuid=" + routeGuid + ", dockerImage=" + dockerImage
				+ ", memory=" + memory + ", disk=" + disk + ", instances="
				+ instances + ", startCommand=" + startCommand + ", state="
				+ state + ", dockerCred=" + dockerCred
				+ ", environmentJsons=" + environmentJsons + "]";
	}
	
	@SuppressWarnings("unchecked")
	public void update(AppMetadataDTO updateTo) {
		
		if (updateTo.getOrg() != null) 
			this.setOrg(updateTo.getOrg());
		
		if (updateTo.getSpace() != null) 
			this.setSpace(updateTo.getSpace());

		if (updateTo.getDomain() != null) 
			this.setDomain(updateTo.getDomain());
		
		if (updateTo.getDockerCred() != null) 
			this.setDockerCred(updateTo.getDockerCred());
		
		if (updateTo.getEnvironmentJsons() != null) 
			this.setEnvironmentJsons( updateTo.getEnvironmentJsons()); 
		
		if (updateTo.getName() != null) 
			this.setName(updateTo.getName());
		
		if (updateTo.getRoute() != null) 
			this.setRoute(updateTo.getRoute());
	
		if (updateTo.getInstances() != 0) 
			this.setInstances(updateTo.getInstances());
		
		if (updateTo.getMemory() != 0) 
			this.setMemory(updateTo.getMemory() );
		
		if (updateTo.getDisk() != 0) 
			this.setDisk(updateTo.getDisk()); 
		
		if (updateTo.getDockerImage() != null) 
			this.setDockerImage(updateTo.getDockerImage()); 
		
	}
	
}
