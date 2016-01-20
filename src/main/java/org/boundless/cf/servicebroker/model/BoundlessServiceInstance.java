package org.boundless.cf.servicebroker.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity(name="boundless_service_instances")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BoundlessServiceInstance extends ServiceInstance {

	// In Boundless, the service instance is actually tied to an app pushed to CF
	// Store the metadata about the apps being pushed as part of a service instance
	@JsonSerialize
	@JsonProperty("bsi_metadata")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private BoundlessServiceInstanceMetadata boundlessSIMetadata; 

	public BoundlessServiceInstance() {  
		super();
	}
	
	public BoundlessServiceInstance(CreateServiceInstanceRequest request) {
		super(request);
		this.initMetadata();
		
		// Pull up the default app config associated with the Service Plan
		PlanConfig appConfig = request.getPlan().getPlanConfig();
		this.resetMetadata(appConfig);
		
		// Let the config be overridden on based on request parameters
		this.updateMetadata(request.getParameters());
		log.debug("Service Instance created: " + this);
	}

	public BoundlessServiceInstance(UpdateServiceInstanceRequest request) {
		super(request);
		this.initMetadata();
		this.updateMetadata(request.getParameters());
		log.debug("Service Instance updated: " + this);
	}
	
	public void update(BoundlessServiceInstance updateTo) {
		super.update(updateTo);
		BoundlessServiceInstanceMetadata updateMetadata = updateTo.getMetadata();
		this.boundlessSIMetadata.update(updateMetadata);
		log.debug("Service Instance updated to: " + this);
	}
	
	@Override
	public String toString() {
		return "BoundlessServiceInstance [" + super.toString() + ", Metadata=" + boundlessSIMetadata
				+ ", parameters=" + parameters + "]";
	}

	public void initMetadata() {
		if (this.boundlessSIMetadata == null) {
			this.boundlessSIMetadata = new BoundlessServiceInstanceMetadata();
			boundlessSIMetadata.generateAndSetId();
		}
	}
	
	public BoundlessServiceInstanceMetadata getMetadata() {
		return boundlessSIMetadata;
	}

	public void resetMetadata(PlanConfig planConfig) {		
		for(BoundlessAppResource resource: this.boundlessSIMetadata.getAppResources()) {
			resource.loadDefaults(planConfig);
		}
	}
	
	public void setMetadata(BoundlessServiceInstanceMetadata bsiMetadata) {
		this.boundlessSIMetadata = bsiMetadata;
		log.info("Setting the metadata for Service Instance: " + this);
	}
	
	private void updateMetadata(Map<String, Object> appMetadataMap) {
		if (appMetadataMap == null) {
			return;
		}
		
		for(String key: appMetadataMap.keySet()) {
			this.boundlessSIMetadata.setMapping(key, appMetadataMap.get(key));
		}
		checkFallback();
		log.info("Service Instance updated: " + this);
	}
	
	private void checkFallback() {
		// if user didnt provide any org or space to push the app to,
		// use the service instance's org & space guids to create the app instances.
		if (boundlessSIMetadata.getOrg() == null) {
			boundlessSIMetadata.setOrgGuid(this.getOrgGuid());
		}
		if (boundlessSIMetadata.getSpace() == null) {
			boundlessSIMetadata.setSpaceGuid(this.getSpaceGuid());
		}
		log.info("Service Instance metadata updated with service instance org/space guid fallback: " + this);
	}

}
