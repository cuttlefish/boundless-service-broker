package org.boundless.cf.servicebroker.model;

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

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "boundless_app_metadata")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BoundlessAppMetadata {

	private static transient final Logger log = Logger.getLogger(BoundlessAppMetadata.class);

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
	@JsonProperty("org_id")
	@Column(nullable = true)
	private String orgGuid;

	@JsonSerialize
	@JsonProperty("space_id")
	@Column(nullable = true)
	private String spaceGuid;

	@JsonSerialize
	@JsonProperty("domain")
	@Column(nullable = true)
	private String domain;	
	
	@JsonSerialize
	@JsonProperty("domain_id")
	@Column(nullable = true)
	private String domainGuid;
	
	@JsonSerialize
	@JsonProperty("geoserver_app")
	@Column(nullable = true)
	private String geoServerApp;
	
	@JsonSerialize
	@JsonProperty("geoserver_app_id")
	@Column(nullable = true)
	private String geoServerAppGuid;
	
	@JsonSerialize
	@JsonProperty("geoserver_uri")
	@Column(nullable = true)
	private String geoServerUri;
	
	@JsonSerialize
	@JsonProperty("geoserver_route_id")
	@Column(nullable = true)
	private String geoServerRouteGuid;
	
	@JsonSerialize
	@JsonProperty("geoserver_docker_image")
	@Column(nullable = true)
	private String geoServerDockerImage;
	
	@JsonSerialize
	@JsonProperty("geoserver_memory")
	@Column(nullable = true)
	private int geoServerMemory = 1024;
	
	@JsonSerialize
	@JsonProperty("geoserver_disk")
	@Column(nullable = true)
	private int geoServerDisk = 2048;
	
	@JsonSerialize
	@JsonProperty("geoserver_instances")
	@Column(nullable = true)
	private int geoServerInstances = 1;
	
	@JsonSerialize
	@JsonProperty("geoserver_command")
	@Column(nullable = true)
	private String geoServerStartCommand;
	
	@JsonSerialize
	@JsonProperty("geoserver_state")
	@Column(nullable = true)	
	private String geoServerState;
		
	@JsonSerialize
	@JsonProperty("geocache_app")
	@Column(nullable = true)
	private String geoCacheApp;

	@JsonSerialize
	@JsonProperty("geocache_app_id")
	@Column(nullable = true)
	private String geoCacheAppGuid;
	
	@JsonSerialize
	@JsonProperty("geocache_uri")
	@Column(nullable = true)
	private String geoCacheUri;
	
	@JsonSerialize
	@JsonProperty("geocache_route_id")
	@Column(nullable = true)
	private String geoCacheRouteGuid;
	
	@JsonSerialize
	@JsonProperty("geocache_docker_image")
	@Column(nullable = true)
	private String geoCacheDockerImage;
	
	@JsonSerialize
	@JsonProperty("geocache_memory")
	@Column(nullable = true)
	private int geoCacheMemory = 1024;
	
	@JsonSerialize
	@JsonProperty("geocache_disk")
	@Column(nullable = true)
	private int geoCacheDisk = 2048;
	
	@JsonSerialize
	@JsonProperty("geocache_instances")
	@Column(nullable = true)
	private int geoCacheInstances = 1;
	
	@JsonSerialize
	@JsonProperty("geocache_command")
	@Column(nullable = true)
	private String geoCacheStartCommand;
	
	@JsonSerialize
	@JsonProperty("geocache_state")
	@Column(nullable = true)	
	private String geoCacheState;
	
	@JsonSerialize
	@JsonProperty("docker_cred")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="docker_cred", joinColumns=@JoinColumn(name="docker_cred_id"))
	protected Map<String,String> docker_cred = new HashMap<String,String>();
	
	@JsonSerialize
	@JsonProperty("geoserver_environment_jsons")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="geoserver_environment_jsons", joinColumns=@JoinColumn(name="geoserver_environment_jsons_id"))
	private Map<String, String> geoServerEnvironmentJsons = new HashMap<String,String>();
	
	@JsonSerialize
	@JsonProperty("geocache_environment_jsons")
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="geocache_environment_jsons", joinColumns=@JoinColumn(name="geocache_environment_jsons_id"))
	private Map<String, String> geoCacheEnvironmentJsons = new HashMap<String,String>();
	
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
	
	public String getGeoServerUri() {
		return geoServerUri;
	}

	public void setGeoServerUri(String geoServerUri) {
		this.geoServerUri = geoServerUri;
	}

	public String getGeoCacheApp() {
		return geoCacheApp;
	}

	public void setGeoCacheApp(String geoCacheApp) {
		this.geoCacheApp = geoCacheApp;
	}

	public String getGeoCacheAppGuid() {
		return geoCacheAppGuid;
	}

	public void setGeoCacheAppGuid(String geoCacheAppGuid) {
		this.geoCacheAppGuid = geoCacheAppGuid;
	}

	public String getGeoCacheUri() {
		return geoCacheUri;
	}

	public void setGeoCacheUri(String geoCacheUri) {
		this.geoCacheUri = geoCacheUri;
	}
	
	public String getGeoCacheRoute() {
		return geoCacheUri;
	}

	public String getGeoCacheRouteGuid() {
		return geoCacheRouteGuid;
	}

	public void setGeoCacheRoute(String geoCacheRoute) {
		setGeoCacheUri(geoCacheRoute);
	}
	
	public void setGeoCacheRouteGuid(String geoCacheRouteGuid) {
		this.geoCacheRouteGuid = geoCacheRouteGuid;
	}

	public String getGeoCacheDockerImage() {
		return geoCacheDockerImage;
	}

	public void setGeoCacheDockerImage(String geoCacheDockerImage) {
		this.geoCacheDockerImage = geoCacheDockerImage;
	}

	public int getGeoCacheMemory() {
		return geoCacheMemory;
	}

	public void setGeoCacheMemory(int geoCacheMemory) {
		this.geoCacheMemory = geoCacheMemory;
	}

	public int getGeoCacheDisk() {
		return geoCacheDisk;
	}

	public void setGeoCacheDisk(int geoCacheDisk) {
		this.geoCacheDisk = geoCacheDisk;
	}

	public int getGeoCacheInstances() {
		return geoCacheInstances;
	}

	public void setGeoCacheInstances(int geoCacheInstances) {
		this.geoCacheInstances = geoCacheInstances;
	}

	public String getGeoCacheStartCommand() {
		return geoCacheStartCommand;
	}

	public void setGeoCacheStartCommand(String geoCacheStartCommand) {
		this.geoCacheStartCommand = geoCacheStartCommand;
	}

	public String getGeoCacheState() {
		return geoCacheState;
	}

	public void setGeoCacheState(String geoCacheState) {
		this.geoCacheState = geoCacheState;
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

	public void setDocker_cred(Map<String, String> docker_cred) {
		this.docker_cred = docker_cred;
	}

	public String getGeoServerAppGuid() {
		return geoServerAppGuid;
	}

	public void setGeoServerAppGuid(String appGuid) {
		this.geoServerAppGuid = appGuid;
	}

	public String getGeoServerRouteGuid() {
		return geoServerRouteGuid;
	}

	public void setGeoServerRouteGuid(String routeGuid) {
		this.geoServerRouteGuid = routeGuid;
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

	public Map<String, String> getGeoServerEnvironmentJsons() {
		return geoServerEnvironmentJsons;
	}

	public void setGeoServerEnvironmentJsons(
			Map<String, String> geoServerEnvironmentJsons) {
		this.geoServerEnvironmentJsons = geoServerEnvironmentJsons;
	}

	public Map<String, String> getGeoCacheEnvironmentJsons() {
		return geoCacheEnvironmentJsons;
	}

	public void setGeoCacheEnvironmentJsons(
			Map<String, String> geoCacheEnvironmentJsons) {
		this.geoCacheEnvironmentJsons = geoCacheEnvironmentJsons;
	}

	public String getGeoServerApp() {
		return geoServerApp;
	}

	public void setGeoServerApp(String app) {
		this.geoServerApp = app;
	}

	public String getGeoServerState() {
		return geoServerState;
	}

	public void setGeoServerState(String state) {
		this.geoServerState = state;
	}

	public String getGeoServerRoute() {
		return geoServerUri;
	}
	
	public void setGeoServerRoute(String routeName) {
		this.geoServerUri = routeName;
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
	
	public String getGeoServerDockerImage() {
		return geoServerDockerImage;
	}
	
	public void setGeoServerDockerImage(String dockerImage) {
		// FIX ME - Default docker image for GeoServer
		if (dockerImage != null)
			this.geoServerDockerImage = dockerImage;
		else
			this.geoServerDockerImage = "jhankes/centos-gs";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geoServerApp == null) ? 0 : geoServerApp.hashCode());
		result = prime * result
				+ ((geoServerDockerImage == null) ? 0 : geoServerDockerImage.hashCode());
		result = prime * result
				+ ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((org == null) ? 0 : org.hashCode());
		result = prime * result + ((orgGuid == null) ? 0 : orgGuid.hashCode());
		result = prime * result + ((geoServerUri == null) ? 0 : geoServerUri.hashCode());
		result = prime * result
				+ ((geoServerRouteGuid == null) ? 0 : geoServerRouteGuid.hashCode());
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
		BoundlessAppMetadata other = (BoundlessAppMetadata) obj;
		if ((id == null) && (other.id != null)) {
				return false;
		} 
		
		return id.equals(other.id);
	}	
	
	@Override
	public String toString() {
		return "BoundlessAppMetadata [id=" + id + ", org=" + org + ", space="
				+ space + ", orgGuid=" + orgGuid + ", spaceGuid=" + spaceGuid
				+ ", domain=" + domain + ", domainGuid=" + domainGuid
				+ ", geoServerApp=" + geoServerApp + ", geoServerAppGuid="
				+ geoServerAppGuid + ", geoServerUri=" + geoServerUri
				+ ", geoServerRouteGuid=" + geoServerRouteGuid
				+ ", geoServerDockerImage=" + geoServerDockerImage
				+ ", geoServerMemory=" + geoServerMemory + ", geoServerDisk="
				+ geoServerDisk + ", geoServerInstances=" + geoServerInstances
				+ ", geoServerStartCommand=" + geoServerStartCommand
				+ ", geoServerState=" + geoServerState + ", geoServerEnvironmentJsons="
				+ geoServerEnvironmentJsons + ", geoCacheApp="
				+ geoCacheApp + ", geoCacheAppGuid=" + geoCacheAppGuid
				+ ", geoCacheUri=" + geoCacheUri + ", geoCacheRouteGuid="
				+ geoCacheRouteGuid + ", geoCacheDockerImage="
				+ geoCacheDockerImage + ", geoCacheMemory=" + geoCacheMemory
				+ ", geoCacheDisk=" + geoCacheDisk + ", geoCacheInstances="
				+ geoCacheInstances + ", geoCacheStartCommand="
				+ geoCacheStartCommand + ", geoCacheState=" + geoCacheState
				+ ", docker_cred=" + docker_cred + ", geoCacheEnvironmentJsons="
				+ geoCacheEnvironmentJsons + "]";
	}

	public int getGeoServerMemory() {
		return geoServerMemory;
	}
	
	public void setGeoServerMemory(int memory) {
		this.geoServerMemory = memory;
	}
	
	public int getGeoServerDisk() {
		return geoServerDisk;
	}
	
	public void setGeoServerDisk(int disk) {
		this.geoServerDisk = disk;
	}
	
	public int getGeoServerInstances() {
		return geoServerInstances;
	}
	public void setGeoServerInstances(int instances) {
		this.geoServerInstances = instances;
	}
	
	public String getGeoServerStartCommand() {
		return geoServerStartCommand;
	}
	
	public void setGeoServerStartCommand(String startCommand) {
		this.geoServerStartCommand = startCommand;
	}
	
	@SuppressWarnings("unchecked")
	public void update(BoundlessAppMetadata updateTo) {
		
		if (updateTo.getOrg() != null) 
			this.setOrg(updateTo.getOrg());
		
		if (updateTo.getSpace() != null) 
			this.setSpace(updateTo.getSpace());

		if (updateTo.getDomain() != null) 
			this.setDomain(updateTo.getDomain());
		
		if (updateTo.getDockerCred() != null) 
			this.setDockerCred(updateTo.getDockerCred());
		
		if (updateTo.getGeoServerEnvironmentJsons() != null) 
			this.setGeoServerEnvironmentJsons( updateTo.getGeoServerEnvironmentJsons()); 
		
		if (updateTo.getGeoCacheEnvironmentJsons() != null) 
			this.setGeoCacheEnvironmentJsons( updateTo.getGeoCacheEnvironmentJsons()); 
		
		if (updateTo.getGeoServerApp() != null) 
			this.setGeoServerApp(updateTo.getGeoServerApp());
		
		if (updateTo.getGeoServerRoute() != null) 
			this.setGeoServerRoute(updateTo.getGeoServerRoute());
	
		if (updateTo.getGeoServerInstances() != 0) 
			this.setGeoServerInstances(updateTo.getGeoServerInstances());
		
		if (updateTo.getGeoServerMemory() != 0) 
			this.setGeoServerMemory(updateTo.getGeoServerMemory() );
		
		if (updateTo.getGeoServerDisk() != 0) 
			this.setGeoServerDisk(updateTo.getGeoServerDisk()); 
		
		if (updateTo.getGeoServerDockerImage() != null) 
			this.setGeoServerDockerImage(updateTo.getGeoServerDockerImage()); 

		if (updateTo.getGeoCacheApp() != null) 
			this.setGeoCacheApp(updateTo.getGeoCacheApp());
		
		if (updateTo.getGeoCacheRoute() != null) 
			this.setGeoCacheRoute(updateTo.getGeoCacheRoute());
	
		if (updateTo.getGeoCacheInstances() != 0) 
			this.setGeoCacheInstances(updateTo.getGeoCacheInstances());
		
		if (updateTo.getGeoCacheMemory() != 0) 
			this.setGeoCacheMemory(updateTo.getGeoCacheMemory() );
		
		if (updateTo.getGeoCacheDisk() != 0) 
			this.setGeoCacheDisk(updateTo.getGeoCacheDisk()); 
		
		if (updateTo.getGeoCacheDockerImage() != null) 
			this.setGeoCacheDockerImage(updateTo.getGeoCacheDockerImage()); 

		if (updateTo.getGeoServerState() != null) 
			this.setGeoServerState(updateTo.getGeoServerState());
		
		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	
		if (updateTo.getSpaceGuid() != null) 
			this.setSpaceGuid(updateTo.getSpaceGuid());

		if (updateTo.getDomainGuid() != null) 
			this.setDomainGuid(updateTo.getDomainGuid());

		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());

		if (updateTo.getGeoServerAppGuid() != null) 
			this.setGeoServerAppGuid(updateTo.getGeoServerAppGuid());
		
		if (updateTo.getGeoServerRouteGuid() != null) 
			this.setGeoServerRouteGuid(updateTo.getGeoServerRouteGuid());		

		if (updateTo.getGeoCacheState() != null) 
			this.setGeoCacheState(updateTo.getGeoCacheState());
		
		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	
		if (updateTo.getSpaceGuid() != null) 
			this.setSpaceGuid(updateTo.getSpaceGuid());

		if (updateTo.getDomainGuid() != null) 
			this.setDomainGuid(updateTo.getDomainGuid());

		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());

		if (updateTo.getGeoCacheAppGuid() != null) 
			this.setGeoCacheAppGuid(updateTo.getGeoCacheAppGuid());
		
		if (updateTo.getGeoCacheRouteGuid() != null) 
			this.setGeoCacheRouteGuid(updateTo.getGeoCacheRouteGuid());

	}
	
	@SuppressWarnings("unchecked")
	public void setMapping(String key, Object val) {
		switch(key) {
			case "org": this.setOrg(val.toString()); break;
			case "docker_cred": this.setDockerCred((HashMap<String, String>) val); break;			
			case "domain": this.setDomain(val.toString()); break;
			case "space": this.setSpace(val.toString()); break;
			case "name": 
			case "geoserver": 
			case "geoserver_name":
				this.setGeoServerApp(val.toString()); break;
			case "geoserver_route": 
			case "geoserver_uri" : 
				this.setGeoServerRoute(val.toString()); break;
			case "geoserver_instances": this.setGeoServerInstances(Integer.parseInt(val.toString())); break;
			case "geoserver_memory": this.setGeoServerMemory(Integer.parseInt(val.toString())); break;
			case "geoserver_disk": this.setGeoServerDisk(Integer.parseInt(val.toString())); break;
			case "geoserver_docker_image": this.setGeoServerDockerImage(val.toString()); break;
			case "geoserver_environment": this.setGeoServerEnvironmentJsons( (HashMap<String, String>) val); break;
			case "geocache_name": 
			case "geocache": 
				this.setGeoCacheApp(val.toString()); break;
			case "geocache_route": 
			case "geocache_uri" : 
				this.setGeoCacheRoute(val.toString()); break;
			case "geocache_instances": this.setGeoCacheInstances(Integer.parseInt(val.toString())); break;
			case "geocache_memory": this.setGeoCacheMemory(Integer.parseInt(val.toString())); break;
			case "geocache_disk": this.setGeoCacheDisk(Integer.parseInt(val.toString())); break;
			case "geocache_docker_image": this.setGeoCacheDockerImage(val.toString()); break;
			case "geocache_environment": this.setGeoCacheEnvironmentJsons( (HashMap<String, String>) val); break;
		default:
			log.info("Could not map parameter: " + key + " with value: " + val);
		}
	}
	public AppMetadata getGeoServerMetadata() {
		if (this.getGeoServerApp() == null)
			return null;
		
		AppMetadata appMetadata = new AppMetadata();		
		appMetadata.setOrg(this.getOrg());
		appMetadata.setOrgGuid(this.getOrgGuid());
		appMetadata.setDockerCred(this.getDockerCred());
		appMetadata.setEnvironmentJsons(this.getGeoServerEnvironmentJsons());
		appMetadata.setDomain(this.getDomain());
		appMetadata.setDomainGuid(this.getDomainGuid());
		appMetadata.setSpace(this.getSpace());
		appMetadata.setSpaceGuid(this.getSpaceGuid());
		appMetadata.setName(this.getGeoServerApp());
		appMetadata.setAppGuid(this.getGeoServerAppGuid());
		appMetadata.setRoute(this.getGeoServerRoute());
		appMetadata.setRouteGuid(this.getGeoServerRouteGuid());
		appMetadata.setInstances(this.getGeoServerInstances());
		appMetadata.setMemory(this.getGeoServerMemory());
		appMetadata.setDisk(this.getGeoServerDisk());
		appMetadata.setDockerImage(this.getGeoServerDockerImage());
		appMetadata.setStartCommand(this.getGeoCacheStartCommand());
		return appMetadata;
	}
	
	public void updateGeoServerMetadata(AppMetadata appMetadata) {
		
		// Update only the guids & state that come back from CFAppManager interactions
		if (appMetadata.getState() != null) 
			this.setGeoServerState(appMetadata.getState());
		
		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());
	
		if (appMetadata.getSpaceGuid() != null) 
			this.setSpaceGuid(appMetadata.getSpaceGuid());

		if (appMetadata.getDomainGuid() != null) 
			this.setDomainGuid(appMetadata.getDomainGuid());

		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());

		if (appMetadata.getAppGuid() != null) 
			this.setGeoServerAppGuid(appMetadata.getAppGuid());
		
		if (appMetadata.getRouteGuid() != null) 
			this.setGeoServerRouteGuid(appMetadata.getRouteGuid());		
		return;
	}

	public AppMetadata getGeoCacheMetadata() {
		
		if (this.getGeoCacheApp() == null)
			return null;

		AppMetadata appMetadata = new AppMetadata();		
		appMetadata.setOrg(this.getOrg());
		appMetadata.setOrgGuid(this.getOrgGuid());
		appMetadata.setDockerCred(this.getDockerCred());
		appMetadata.setEnvironmentJsons(this.getGeoServerEnvironmentJsons());
		appMetadata.setDomain(this.getDomain());
		appMetadata.setDomainGuid(this.getDomainGuid());
		appMetadata.setSpace(this.getSpace());
		appMetadata.setSpaceGuid(this.getSpaceGuid());
		appMetadata.setName(this.getGeoServerApp());
		appMetadata.setAppGuid(this.getGeoCacheAppGuid());
		appMetadata.setRoute(this.getGeoCacheRoute());
		appMetadata.setRouteGuid(this.getGeoCacheRouteGuid());
		appMetadata.setInstances(this.getGeoCacheInstances());
		appMetadata.setMemory(this.getGeoCacheMemory());
		appMetadata.setDisk(this.getGeoCacheDisk());
		appMetadata.setDockerImage(this.getGeoCacheDockerImage());
		appMetadata.setStartCommand(this.getGeoCacheStartCommand());
		return appMetadata;
	}
	
	public void updateGeoCacheMetadata(AppMetadata appMetadata) {
		
		// Update only the guids & state that come back from CFAppManager interactions
		if (appMetadata.getState() != null) 
			this.setGeoCacheState(appMetadata.getState());
		
		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());
	
		if (appMetadata.getSpaceGuid() != null) 
			this.setSpaceGuid(appMetadata.getSpaceGuid());

		if (appMetadata.getDomainGuid() != null) 
			this.setDomainGuid(appMetadata.getDomainGuid());

		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());

		if (appMetadata.getAppGuid() != null) 
			this.setGeoCacheAppGuid(appMetadata.getAppGuid());
		
		if (appMetadata.getRouteGuid() != null) 
			this.setGeoCacheRouteGuid(appMetadata.getRouteGuid());
		return;
	}
	
}
