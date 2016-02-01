package org.boundless.cf.servicebroker.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.model.dto.AppMetadataDTO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/*
 * Holds information about the Boundless Apps running on CF
 * There are two types of Apps: geoserver & geocache, each with their own memory/disk/instances/command/docker image
 * They are represented by BoundlessAppResource
 * 
 */
@Entity
@Table(name = "boundless_serviceinstance_metadata")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BoundlessServiceInstanceMetadata {

	private static transient final Logger log = Logger.getLogger(BoundlessServiceInstanceMetadata.class);
	
	@Id
	@JsonSerialize
	private String id;

	@JsonSerialize
	@JsonProperty("org")
	@Column(nullable = true)
	private String org;
	
	@JsonSerialize
	@JsonProperty("space")
	@Column(nullable = true)
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
	@OneToMany(mappedBy="boundlessServiceInstanceMetadata", orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Set<BoundlessAppResource> appResources = new HashSet<BoundlessAppResource>();

	
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

	public synchronized Set<BoundlessAppResource> getAppResources() {		
		if (appResources.size() == 0) {
			for(String type: BoundlessAppResourceConstants.getTypes()) {
				BoundlessAppResource resource = new BoundlessAppResource(type);
				appResources.add(resource);
				resource.setBoundlessAppMetadata(this);
			}
		}
		return appResources;
	}

	public synchronized void setAppResources(Set<BoundlessAppResource> appResources) {
		if (appResources != null) {
			for(BoundlessAppResource resource: this.appResources) {
				resource.setBoundlessAppMetadata(null);
			}
			this.appResources = appResources;
			for(BoundlessAppResource resource: this.appResources) {
				resource.setBoundlessAppMetadata(this);
			}
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
	
	
	@SuppressWarnings("unchecked")
	public void update(BoundlessServiceInstanceMetadata updateTo) {
		
		if (updateTo.getOrg() != null) 
			this.setOrg(updateTo.getOrg());
		
		if (updateTo.getSpace() != null) 
			this.setSpace(updateTo.getSpace());

		if (updateTo.getDomain() != null) 
			this.setDomain(updateTo.getDomain());
		
		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	
		if (updateTo.getSpaceGuid() != null) 
			this.setSpaceGuid(updateTo.getSpaceGuid());

		if (updateTo.getDomainGuid() != null) 
			this.setDomainGuid(updateTo.getDomainGuid());

		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	
		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	
		if (updateTo.getSpaceGuid() != null) 
			this.setSpaceGuid(updateTo.getSpaceGuid());

		if (updateTo.getDomainGuid() != null) 
			this.setDomainGuid(updateTo.getDomainGuid());

		if (updateTo.getOrgGuid() != null) 
			this.setOrgGuid(updateTo.getOrgGuid());
	}
	
	public void overrideResourceDefns(Map<String, Object> appMetadataMap) {
		if (appMetadataMap == null || appMetadataMap.size() == 0)
			return;
		
		HashMap<String, String> newEnvMap = new HashMap<String, String>();
		for(String key: appMetadataMap.keySet()) {
			Object val = appMetadataMap.get(key);
			if (val == null)
				continue;
			
			boolean paramHandled = false;
			switch(key) {
			case "org": this.setOrg(val.toString()); break;
			case "domain": this.setDomain(val.toString()); break;
			case "space": this.setSpace(val.toString()); break;
			default:
				for (String type : BoundlessAppResourceConstants.getTypes()) {
					if (BoundlessAppResourceConstants.isOfType(key, type)) {
						paramHandled = setResourceMapping(type, key, val);
						if (paramHandled);
						break;
					}
				}
				if (!paramHandled) {
					log.info("Could not map parameter: " + key + " with value: " + val + ", adding to env variables");
					newEnvMap.put(key, (val == null? null : val.toString()));
				}
			}
		}
		
		// For all new/unknown parameters, add them into environment variables for both resources.
		for (BoundlessAppResource resource : this.getAppResources()) { 
			resource.getEnvironmentJsons().putAll(newEnvMap);
		}
	}

	public boolean setResourceMapping(String type, String key, Object val) {
		int index = type.length() + 1;
		String modKey = key.substring(index);
		
		BoundlessAppResource resource = getResource(type);
		if (resource == null) {
			resource = new BoundlessAppResource(type);
			this.addResource(resource);
			resource.setBoundlessAppMetadata(this);
		}
		
		switch(modKey.toLowerCase()) {
			case "name": 
				resource.setAppName(val.toString()); 
				break;
			case "route": 
			case "uri" : 
				resource.setRoute(val.toString()); 
				break;
			case "instances": 
				resource.setInstances(Integer.parseInt(val.toString())); 
				break;
			case "memory": 
				resource.setMemory(Integer.parseInt(val.toString())); 
				break;
			case "disk": 
				resource.setDisk(Integer.parseInt(val.toString())); 
				break;
			case "docker_image": 
				resource.setDockerImage(val.toString()); 
				break;
		    case "docker_cred": 
		    	resource.setDockerCred((HashMap<String, String>) val); 
		    	break;
			case "environment": 
				resource.setEnvironmentJsons( (Map<String,String>) val); 
				break;
			default:
				log.info("Could not map app parameter: " + key + " with value: " + val);
				return false;
		}
		return true;
	}

	public void updateResourceDefns(Map<String, Object> appMetadataMap) {
		if (appMetadataMap == null || appMetadataMap.size() == 0)
			return;
		
		for(String key: appMetadataMap.keySet()) {
			Object val = appMetadataMap.get(key);
			if (val == null)
				continue;

			for (String type : BoundlessAppResourceConstants.getTypes()) {
				if (BoundlessAppResourceConstants.isOfType(key, type)) {
					updateResourceMapping(type, key, val);
				}
			}
		}
	}
	
	/*
	 * Only update specific attributes like instances, memory, disk, env.
	 */
	public void updateResourceMapping(String type, String key, Object val) {
		int index = type.length() + 1;
		String modKey = key.substring(index);
		
		BoundlessAppResource resource = getResource(type);
		if (resource == null) {
			log.info("Ignoring update of Resource type: " + type + " as it was not created earlier");
			return;
		}
		
		// We will only allow override of the memory, disk, instances or env variables
		switch(modKey.toLowerCase()) {
			case "instances": 
				resource.setInstances(Integer.parseInt(val.toString())); 
				break;
			case "memory": 
				resource.setMemory(Integer.parseInt(val.toString())); 
				break;
			case "disk": 
				resource.setDisk(Integer.parseInt(val.toString())); 
				break;
			case "environment": 
				resource.setEnvironmentJsons( (Map<String,String>) val); 
				break;
			default:
				log.debug("Ignoring update of app parameter: " + key + " with value: " + val);
		}
	}

	public synchronized void addResource(BoundlessAppResource resource) {
		if (!appResources.contains(resource)) {
			this.appResources.add(resource);
		}
	}
	
	public synchronized BoundlessAppResource getResource(String type) {
		if ( type == null || appResources.size() == 0 )
			return null;
		
		BoundlessAppResource targetResource = null;
		for (Object resource: appResources.toArray()) {
			BoundlessAppResource rsc = (BoundlessAppResource)resource;
			if (rsc.getType().equals(type)) {
				targetResource = (BoundlessAppResource)resource;
				break;
			}
		}
		
		return targetResource;		
	}

	public AppMetadataDTO generateAppMetadata(String type) {
		
		BoundlessAppResource targetResource = getResource(type);		
		if ( targetResource == null 
				|| targetResource.getAppName() == null 
				|| targetResource.getInstances() == 0
				|| targetResource.getDockerImage() == null)
			return null;		
		
		AppMetadataDTO appMetadata = new AppMetadataDTO();		
		appMetadata.setOrg(this.getOrg());
		appMetadata.setOrgGuid(this.getOrgGuid());
		appMetadata.setDomain(this.getDomain());
		appMetadata.setDomainGuid(this.getDomainGuid());
		appMetadata.setSpace(this.getSpace());
		appMetadata.setSpaceGuid(this.getSpaceGuid());
		appMetadata.setType(type);
		
		targetResource.dumpMetadata(appMetadata);
		return appMetadata;
	}
	
	public void updateFromAppMetadata(AppMetadataDTO appMetadata) {
		String type = appMetadata.getType();
		BoundlessAppResource targetResource = getResource(type);		
		if ( targetResource == null)
			return;
		
		// Update only the guids & state that come back from CFAppManager interactions
		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());
	
		if (appMetadata.getSpaceGuid() != null) 
			this.setSpaceGuid(appMetadata.getSpaceGuid());

		if (appMetadata.getDomainGuid() != null) 
			this.setDomainGuid(appMetadata.getDomainGuid());

		if (appMetadata.getOrgGuid() != null) 
			this.setOrgGuid(appMetadata.getOrgGuid());

		targetResource.update(appMetadata);
		return;
	}
	
	public boolean isTargetSpaceDefined() {
		return this.space != null;
	}
	
	public boolean isTargetOrgDefined() {
		return this.org != null;
	}

	@Override
	public String toString() {
		return "BoundlessServiceInstanceMetadata [id=" + id + ", org=" + org + ", space="
				+ space + ", orgGuid=" + orgGuid + ", spaceGuid=" + spaceGuid
				+ ", domain=" + domain + ", domainGuid=" + domainGuid
				+ ", appResources=" + appResources + ", serviceInstanceId=?? ]";
	}

}
