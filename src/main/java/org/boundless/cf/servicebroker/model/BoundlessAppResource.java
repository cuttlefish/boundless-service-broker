package org.boundless.cf.servicebroker.model;

import java.lang.reflect.Type;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//Represents each type of application that would be pushed to CF as part of one single service instance creation
// There can GeoServer, GeoCache and any other additional components..
@Entity
@Table(name = "boundless_app_resource")
@JsonIgnoreProperties({ "boundless_app_metadata" })
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BoundlessAppResource {

	private static transient final Logger log = Logger.getLogger(BoundlessAppResource.class);

	@Id
	@JsonSerialize
	private String id;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="boundless_serviceinstance_metadata_id", insertable = true, updatable = false)
	// Mark insertable false for compound keys, shared primary key, cascaded key
	private BoundlessServiceInstanceMetadata boundlessServiceInstanceMetadata;
	
	@JsonSerialize
	@JsonProperty("app_name")
	@Column(nullable = true)
	private String appName;
	
	@JsonSerialize
	@JsonProperty("type")
	@Column(nullable = true)
	private String type;

	@JsonSerialize
	@JsonProperty("app_id")
	@Column(nullable = true)
	private String appGuid;
	
	@JsonSerialize
	@JsonProperty("route")
	@Column(nullable = true)
	private String route;
	
	@JsonSerialize
	@JsonProperty("route_id")
	@Column(nullable = true)
	private String routeGuid;
	
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
	protected Map<String,String> dockerCred = new HashMap<String,String>();
	
	@JsonSerialize
	@JsonProperty("environment_jsons")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="environmentJsons", joinColumns=@JoinColumn(name="environmentJsons_id"))
	private Map<String,String> environmentJsons;
	
	public BoundlessAppResource() { 
		generateAndSetId();
	}

	public BoundlessAppResource(String type) {
		this();
		this.type = type;
	}
	
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
	
	public void loadDefaults(PlanConfig config) {
		switch(this.type) {		
		case BoundlessAppResourceType.GEO_SERVER_TYPE:
			this.setDockerImage(config.getGeoServerDockerUri());
			this.setInstances(config.getGeoServerInstance());
			this.setMemory(config.getGeoServerMemory());
			this.setDisk(config.getGeoServerDisk());
			this.setDockerImage(config.getGeoServerDockerUri());
			break;		
		case BoundlessAppResourceType.GEO_CACHE_TYPE:
			this.setDockerImage(config.getGeoCacheDockerUri());
			this.setInstances(config.getGeoCacheInstance());
			this.setMemory(config.getGeoCacheMemory());
			this.setDisk(config.getGeoCacheDisk());
			this.setDockerImage(config.getGeoCacheDockerUri());
		default:
			break;
		}
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
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String name) {
		this.appName = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getDockerImage() {
		return dockerImage;
	}

	public void setDockerImage(String dockerImage) {
		this.dockerImage = dockerImage;
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

	public Map<String, String> getDockerCred() {
		return dockerCred;
	}

	public void setDockerCred(Map<String, String> dockerCred) {
		this.dockerCred = dockerCred;
	}

	public Map<String,String> getEnvironmentJsons() {
		return environmentJsons;
	}

	public void setEnvironmentJsons(Map<String,String> environmentJsons) {
		this.environmentJsons = environmentJsons;
	}
	
	public void addToEnvironment(String key, Object value) {
		//Gson gson = createGson();
		//HashMap map = gson.fromJson(this.getEnvironmentJsons(), HashMap.class);
		//map.put(key, value);
		this.environmentJsons.put(key, "" + value);
	}
	
	public void removeFromEnvironment(String key, Object value) {
		//Gson gson = createGson();
		//HashMap map = gson.fromJson(this.getEnvironmentJsons(), HashMap.class);
		//map.put(key, value);
		this.environmentJsons.remove(key);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public BoundlessServiceInstanceMetadata getBoundlessAppMetadata() {
		return boundlessServiceInstanceMetadata;
	}

	public void setBoundlessAppMetadata(BoundlessServiceInstanceMetadata boundlessAppMetadata) {
		this.boundlessServiceInstanceMetadata = boundlessAppMetadata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result
				+ ((dockerImage == null) ? 0 : dockerImage.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result
				+ ((routeGuid == null) ? 0 : routeGuid.hashCode());
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
		BoundlessAppResource other = (BoundlessAppResource) obj;
		if ((id == null) && (other.id != null)) {
				return false;
		} 
		
		return id.equals(other.id);
	}	
	
	@SuppressWarnings("unchecked")
	public void update(BoundlessAppResource updateTo) {
		
		if (updateTo.getDockerCred() != null) 
			this.setDockerCred(updateTo.getDockerCred());
		
		if (updateTo.getEnvironmentJsons() != null) 
			this.setEnvironmentJsons( updateTo.getEnvironmentJsons()); 
		
		if (updateTo.getAppName() != null) 
			this.setAppName(updateTo.getAppName());
		
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

		if (updateTo.getState() != null) 
			this.setState(updateTo.getState()); 
	}
	
	@SuppressWarnings("unchecked")
	public void setMapping(String key, Object val) {
		switch(key) {
			case "docker_cred": this.setDockerCred((HashMap<String, String>) val); break;		
			case "app": 
			case "name": 
			case "app_name": 
				this.setAppName(val.toString()); break;
			case "route": 
			case "uri" : 
				this.setRoute(val.toString()); break;
			case "instances": this.setInstances(Integer.parseInt(val.toString())); break;
			case "memory": this.setMemory(Integer.parseInt(val.toString())); break;
			case "disk": this.setDisk(Integer.parseInt(val.toString())); break;
			case "start_command": this.setStartCommand(val.toString()); break;
			case "docker_image": this.setDockerImage(val.toString()); break;
			case "environment": this.setEnvironmentJsons( (Map<String,String>) val); break;
		default:
			log.info("Could not map parameter: " + key + " with value: " + val);
		}
	}
	
	public AppMetadata dumpMetadata(AppMetadata appMetadata) {
		appMetadata.setName(this.getAppName());
		appMetadata.setAppGuid(this.getAppGuid());
		appMetadata.setRoute(this.getRoute());
		appMetadata.setRouteGuid(this.getRouteGuid());
		appMetadata.setInstances(this.getInstances());
		appMetadata.setMemory(this.getMemory());
		appMetadata.setDisk(this.getDisk());
		appMetadata.setDockerImage(this.getDockerImage());
		appMetadata.setStartCommand(this.getStartCommand());
		appMetadata.setDockerCred(this.getDockerCred());
		appMetadata.setEnvironmentJsons(convertToObjectMap(this.getEnvironmentJsons()));
		
		return appMetadata;
	}
	
	public void update(AppMetadata appMetadata) {
		
		// Update only the guids & state that come back from CFAppManager interactions
		if (appMetadata.getState() != null) 
			this.setState(appMetadata.getState());

		if (appMetadata.getAppGuid() != null) 
			this.setAppGuid(appMetadata.getAppGuid());
		
		if (appMetadata.getRouteGuid() != null) 
			this.setRouteGuid(appMetadata.getRouteGuid());	
		
		return;
	}

	@Override
	public String toString() {
		return "BoundlessAppResource [appName=" + appName + ", type=" + type
				+ ", appGuid=" + appGuid + ", route=" + route + ", routeGuid="
				+ routeGuid + ", dockerImage=" + dockerImage + ", memory="
				+ memory + ", disk=" + disk + ", instances=" + instances
				+ ", startCommand=" + startCommand + ", state=" + state
				+ ", dockerCred=" + dockerCred + ", environmentJsons="
				+ environmentJsons + ", serviceInstanceMetadataId=" + this.boundlessServiceInstanceMetadata.getId() + "]";
	}

	private static Map<String, Object> convertToObjectMap(Map<String, String> srcMap) {
		HashMap<String, Object> targetMap = new HashMap<String, Object>();
		for(String key: srcMap.keySet()) {
			String val = srcMap.get(key);
			Object nativeVal = val;
			try {
				nativeVal = Double.valueOf(val);
				Double double1 = (Double)nativeVal;
				if (double1.doubleValue() == double1.intValue()) {
					nativeVal = new Integer(double1.intValue());
				}

			} catch(NumberFormatException ipe) {
				String lowerVal = val.trim().toLowerCase();

				if (lowerVal.equals("true") || lowerVal.equals("false")) {
					nativeVal = Boolean.valueOf(val);
				}
			}
			targetMap.put(key, nativeVal);
		}
		return targetMap;
	}
	
	private static Gson createGson() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {

			public JsonElement serialize(Double src, Type typeOfSrc,
					JsonSerializationContext context) {
				Integer value = (int)Math.round(src);
				return new JsonPrimitive(value);
			}
		});
		return gsonBuilder.create();
	}
	
}
