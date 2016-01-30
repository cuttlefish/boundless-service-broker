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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "planconfigs")
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer" })
public class PlanConfig {

	private static transient final Logger log = Logger.getLogger(PlanConfig.class);
	
	@Id
    private String id;
	
	@Column(name="geoserver_docker_uri", nullable = true)
	private String geoServerDockerUri;

	@Column(name="geowebcache_docker_uri", nullable = true)
	private String geoWebCacheDockerUri;

	@Column(name="geoserver_start_command", nullable = true)
	private String geoServerStartCommand;

	@Column(name="geowebcache_start_command", nullable = true)
	private String geoWebCacheStartCommand;

	@Column(name="geoserver_disk", nullable = true)
	private int geoServerDisk;

	@Column(name="geowebcache_disk", nullable = true)
	private int geoWebCacheDisk;

	@Column(name="geoserver_memory", nullable = true)
	private int geoServerMemory;

	@Column(name="geowebcache_memory", nullable = true)
	private int geoWebCacheMemory;

	@Column(name="geoserver_instance", nullable = true)
	private int geoServerInstance;

	@Column(name="geowebcache_instance", nullable = true)
	private int geoWebCacheInstance;

	// any "other" tags/key-value pairs    
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="planconfig_other_attributes", joinColumns=@JoinColumn(name="planconfig_other_attrib_id"))
	protected Map<String,String> otherAttribs = new HashMap<String,String>();
		
    // "any getter" needed for serialization    
    @JsonAnyGetter
    public Map<String,String> any() {
    	return otherAttribs;
    }

    @JsonAnySetter
    public void set(String name, String value) {
    	otherAttribs.put(name, value);
    }

	public String generateId() {		
		//return UUID.nameUUIDFromBytes((this.getServiceName() + ":" + this.getName()).getBytes()).toString();
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

	public synchronized String getId () { 
		if (id == null)
			generateAndSetId();
		return id; 
	}

	
	public String getGeoServerStartCommand() {
		return geoServerStartCommand;
	}

	public void setGeoServerStartCommand(String geoServerStartCommand) {
		this.geoServerStartCommand = geoServerStartCommand;
	}

	public String getGeoWebCacheStartCommand() {
		return geoWebCacheStartCommand;
	}

	public void setGeoWebCacheStartCommand(String geoWebCacheStartCommand) {
		this.geoWebCacheStartCommand = geoWebCacheStartCommand;
	}

	public String getGeoServerDockerUri() {
		return geoServerDockerUri;
	}

	public void setGeoServerDockerUri(String geoserver_docker_uri) {
		this.geoServerDockerUri = geoserver_docker_uri;
	}

	public String getGeoWebCacheDockerUri() {
		return geoWebCacheDockerUri;
	}

	public void setGeoWebCacheDockerUri(String geowebcache_docker_uri) {
		this.geoWebCacheDockerUri = geowebcache_docker_uri;
	}

	public int getGeoServerDisk() {
		return geoServerDisk;
	}

	public void setGeoServerDisk(int geoserver_disk) {
		this.geoServerDisk = geoserver_disk;
	}

	public int getGeoWebCacheDisk() {
		return geoWebCacheDisk;
	}

	public void setGeoWebCacheDisk(int geowebcache_disk) {
		this.geoWebCacheDisk = geowebcache_disk;
	}

	public int getGeoServerMemory() {
		return geoServerMemory;
	}

	public void setGeoServerMemory(int geoserver_memory) {
		this.geoServerMemory = geoserver_memory;
	}

	public int getGeoWebCacheMemory() {
		return geoWebCacheMemory;
	}

	public void setGeoWebCacheMemory(int geowebcache_memory) {
		this.geoWebCacheMemory = geowebcache_memory;
	}

	public int getGeoServerInstance() {
		return geoServerInstance;
	}

	public void setGeoServerInstance(int geoServerInstance) {
		this.geoServerInstance = geoServerInstance;
	}

	public int getGeoWebCacheInstance() {
		return geoWebCacheInstance;
	}

	public void setGeoWebCacheInstance(int geoWebCacheInstance) {
		this.geoWebCacheInstance = geoWebCacheInstance;
	}

	@SuppressWarnings("unchecked")
	public Object get(String key) {
		switch(key) {
			case "geowebcache_instances": return this.getGeoWebCacheInstance();
			case "geowebcache_memory": return this.getGeoWebCacheMemory();
			case "geowebcache_disk": return this.getGeoWebCacheDisk();
			case "geowebcache_start_command": return this.getGeoWebCacheStartCommand();
			case "geowebcache_docker_uri": return this.getGeoWebCacheDockerUri();
			case "geoserver_instances": return this.getGeoServerInstance();
			case "geoserver_memory": return this.getGeoServerMemory();
			case "geoserver_disk": return this.getGeoServerDisk();
			case "geoserver_start_command": return this.getGeoServerStartCommand();
			case "geoserver_docker_uri": return this.getGeoServerDockerUri();
		default:
			log.info("Could not map parameter: " + key);
			return null;
		}
	}

	@Override
	public String toString() {
		return "AppConfig [id=" + id + ", geoServerDockerUri="
				+ geoServerDockerUri + ", geoWebCacheDockerUri="
				+ geoWebCacheDockerUri + ", geoServerDisk="
				+ geoServerDisk + ", geoWebCacheDisk=" + geoWebCacheDisk
				+ ", geoServerMemory=" + geoServerMemory + ", geoWebCacheMemory="
				+ geoWebCacheMemory + ", geoServerInstance=" + geoServerInstance
				+ ", geoWebCacheInstance=" + geoWebCacheInstance + ", otherAttribs="
				+ otherAttribs + "]";
	}

	public void update(PlanConfig planDetails) {

		this.geoWebCacheDockerUri = planDetails.getGeoWebCacheDockerUri();
		this.geoServerDockerUri = planDetails.getGeoServerDockerUri();

		this.geoServerMemory = planDetails.geoServerMemory;
		this.geoWebCacheMemory = planDetails.geoWebCacheMemory;

		this.geoServerInstance = planDetails.geoServerInstance;
		this.geoWebCacheInstance = planDetails.geoWebCacheInstance;

		this.geoServerDisk = planDetails.geoServerDisk;
		this.geoWebCacheDisk = planDetails.geoWebCacheDisk;

		this.geoServerInstance = planDetails.geoServerInstance;
		this.geoWebCacheInstance = planDetails.geoWebCacheInstance;

		this.otherAttribs.clear();
		
		for(String key:planDetails.otherAttribs.keySet()) {
			otherAttribs.put(key, planDetails.otherAttribs.get(key) );
		}
	}




}
