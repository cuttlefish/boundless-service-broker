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

	@Column(name="geocache_docker_uri", nullable = true)
	private String geoCacheDockerUri;

	@Column(name="geoserver_start_command", nullable = true)
	private String geoServerStartCommand;

	@Column(name="geocache_start_command", nullable = true)
	private String geoCacheStartCommand;

	@Column(name="geoserver_disk", nullable = true)
	private int geoServerDisk;

	@Column(name="geocache_disk", nullable = true)
	private int geoCacheDisk;

	@Column(name="geoserver_memory", nullable = true)
	private int geoServerMemory;
	
	@Column(name="geocache_memory", nullable = true)
	private int geoCacheMemory;

	@Column(name="geoserver_instance", nullable = true)
	private int geoServerInstance;

	@Column(name="geocache_instance", nullable = true)
	private int geoCacheInstance;

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

	public String getGeoCacheStartCommand() {
		return geoCacheStartCommand;
	}

	public void setGeoCacheStartCommand(String geoCacheStartCommand) {
		this.geoCacheStartCommand = geoCacheStartCommand;
	}

	public String getGeoServerDockerUri() {
		return geoServerDockerUri;
	}

	public void setGeoServerDockerUri(String geoserver_docker_uri) {
		this.geoServerDockerUri = geoserver_docker_uri;
	}

	public String getGeoCacheDockerUri() {
		return geoCacheDockerUri;
	}

	public void setGeoCacheDockerUri(String geocache_docker_uri) {
		this.geoCacheDockerUri = geocache_docker_uri;
	}

	public int getGeoServerDisk() {
		return geoServerDisk;
	}

	public void setGeoServerDisk(int geoserver_disk) {
		this.geoServerDisk = geoserver_disk;
	}

	public int getGeoCacheDisk() {
		return geoCacheDisk;
	}

	public void setGeoCacheDisk(int geocache_disk) {
		this.geoCacheDisk = geocache_disk;
	}

	public int getGeoServerMemory() {
		return geoServerMemory;
	}

	public void setGeoServerMemory(int geoserver_memory) {
		this.geoServerMemory = geoserver_memory;
	}

	public int getGeoCacheMemory() {
		return geoCacheMemory;
	}

	public void setGeoCacheMemory(int geocache_memory) {
		this.geoCacheMemory = geocache_memory;
	}
	
	public int getGeoServerInstance() {
		return geoServerInstance;
	}

	public void setGeoServerInstance(int geoServerInstance) {
		this.geoServerInstance = geoServerInstance;
	}

	public int getGeoCacheInstance() {
		return geoCacheInstance;
	}

	public void setGeoCacheInstance(int geoCacheInstance) {
		this.geoCacheInstance = geoCacheInstance;
	}

	@SuppressWarnings("unchecked")
	public Object get(String key) {
		switch(key) {
			case "geocache_instances": return this.getGeoCacheInstance();
			case "geocache_memory": return this.getGeoCacheMemory();
			case "geocache_disk": return this.getGeoCacheDisk();
			case "geocache_start_command": return this.getGeoCacheStartCommand();
			case "geocache_docker_uri": return this.getGeoCacheDockerUri();
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
				+ geoServerDockerUri + ", geoCacheDockerUri="
				+ geoCacheDockerUri + ", geoServerDisk="
				+ geoServerDisk + ", geoCacheDisk=" + geoCacheDisk
				+ ", geoServerMemory=" + geoServerMemory + ", geoCacheMemory="
				+ geoCacheMemory + ", geoServerInstance=" + geoServerInstance
				+ ", geoCacheInstance=" + geoCacheInstance + ", otherAttribs="
				+ otherAttribs + "]";
	}

	public void update(PlanConfig planDetails) {
		
		this.geoCacheDockerUri = planDetails.getGeoCacheDockerUri();
		this.geoServerDockerUri = planDetails.getGeoServerDockerUri();
		
		this.geoServerMemory = planDetails.geoServerMemory;
		this.geoCacheMemory = planDetails.geoCacheMemory;
		
		this.geoServerInstance = planDetails.geoServerInstance;
		this.geoCacheInstance = planDetails.geoCacheInstance;
		
		this.geoServerDisk = planDetails.geoServerDisk;
		this.geoCacheDisk = planDetails.geoCacheDisk;
		
		this.geoServerInstance = planDetails.geoServerInstance;
		this.geoCacheInstance = planDetails.geoCacheInstance;
		
		this.otherAttribs.clear();
		
		for(String key:planDetails.otherAttribs.keySet()) {
			otherAttribs.put(key, planDetails.otherAttribs.get(key) );
		}
	}




}
