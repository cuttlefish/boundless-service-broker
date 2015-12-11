package org.boundless.cf.servicebroker.servicebroker.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boundless.cf.servicebroker.servicebroker.controller.ServiceBrokerController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "app_metadata")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class AppMetadata {

	private static transient Log log = LogFactory.getLog(AppMetadata.class);

	@Id
	private String id;

	@JsonSerialize
	@JsonProperty("org")
	@Column(nullable = false)
	private String org;
	
	@JsonSerialize
	@JsonProperty("space")
	@Column(nullable = false)
	private String space;

	@JsonSerialize
	@JsonProperty("app")
	@Column(nullable = false)
	private String app;
	
	@JsonSerialize
	@JsonProperty("org_id")
	@Column(nullable = true)
	private String orgGuid;

	@JsonSerialize
	@JsonProperty("space_id")
	@Column(nullable = true)
	private String spaceGuid;

	@JsonSerialize
	@JsonProperty("app_id")
	@Column(nullable = true)
	private String appGuid;
	
	@JsonSerialize
	@JsonProperty("uri")
	@Column(nullable = true)
	private String uri;
	
	@JsonSerialize
	@JsonProperty("route_id")
	@Column(nullable = true)
	private String routeGuid;
	
	@JsonSerialize
	@JsonProperty("domain_name")
	@Column(nullable = true)
	private String domainName;
	
	@JsonSerialize
	@JsonProperty("docker_image")
	@Column(nullable = true)
	private String dockerImage;
	
	@JsonSerialize
	@JsonProperty("memory")
	@Column(nullable = true)
	private int memory = 1024;
	
	@JsonSerialize
	@JsonProperty("disk")
	@Column(nullable = true)
	private int disk = 2048;
	
	@JsonSerialize
	@JsonProperty("instances")
	@Column(nullable = true)
	private int instances = 1;
	
	@JsonSerialize
	@JsonProperty("command")
	@Column(nullable = true)
	private String startCommand;
	
	@JsonSerialize
	@JsonProperty("state")
	@Column(nullable = true)	
	private String state;
	
	@JsonSerialize
	@JsonProperty("docker_cred")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="docker_cred", joinColumns=@JoinColumn(name="docker_cred_id"))
	protected Map<String,String> docker_cred = new HashMap<String,String>();
	
	@JsonSerialize
	@JsonProperty("environment_jsons")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="environment_jsons", joinColumns=@JoinColumn(name="environment_jsons_id"))
	private Map<String, String> environmentJsons = new HashMap<String,String>();
	
	public String generateId() {		
		return UUID.randomUUID().toString();
	}
	
	public synchronized void generateAndSetId() {
		if (this.id == null)
			this.id = generateId();
	}
	
	public synchronized void setId(String pk) {
		if ((this.id == null) && (pk != null))
			this.id = pk; 
		else
			generateAndSetId();
	}
	
	public synchronized String getId() {
		if (id == null)
			generateAndSetId();
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public void setMapping(String key, Object val) {
		switch(key) {
			case "org": this.setOrg(val.toString()); break;
			case "app": this.setApp(val.toString()); break;
			case "space": this.setSpace(val.toString()); break;
			case "uri": this.setUri(val.toString()); break;
			case "instances": this.setInstances(Integer.parseInt(val.toString())); break;
			case "memory": this.setMemory(Integer.parseInt(val.toString())); break;
			case "disk": this.setDisk(Integer.parseInt(val.toString())); break;
			case "docker_image": this.setDockerImage(val.toString()); break;
			case "docker_cred": this.setDockerCred((HashMap<String, String>) val); break;
			case "environment": this.setEnvironmentJsons( (HashMap<String, String>) val); break;
		default:
			log.info("Could not map parameter: " + key + " with value: " + val);
		}
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
	
	public Map<String, String> getDockerCred() {
		return docker_cred;
	}

	public void setDockerCred(Map<String, String> docker_cred) {
		this.docker_cred = docker_cred;
	}

	public Map<String, String> getEnvironmentJsons() {
		return environmentJsons;
	}

	public void setEnvironmentJsons(Map<String, String> environmentJsons) {
		this.environmentJsons = environmentJsons;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String route) {
		this.uri = route;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRouteName() {
		return uri;
	}
	
	public void setRouteName(String routeName) {
		this.uri = routeName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
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
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime * result
				+ ((dockerImage == null) ? 0 : dockerImage.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AppMetadata other = (AppMetadata) obj;
		if ((id == null) && (other.id != null)) {
				return false;
		} 
		
		return id.equals(other.id);
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
	
}
