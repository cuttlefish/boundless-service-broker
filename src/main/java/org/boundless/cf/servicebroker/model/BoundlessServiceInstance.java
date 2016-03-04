package org.boundless.cf.servicebroker.model;

import java.util.Map;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.boundless.cf.servicebroker.model.dto.CreateServiceInstanceRequest;
import org.boundless.cf.servicebroker.model.dto.UpdateServiceInstanceRequest;

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
		// Pull up the default app config associated with the Service Plan
		// And use that to initialize the default settings for service instances
		
		this.initMetadata(request);
		log.debug("Service Instance created: " + this);
	}

	public int generateRandom() {
		return (new Random()).nextInt(1000);
	}
	
	public void initMetadata(CreateServiceInstanceRequest request) {
		Plan plan = request.getPlan();
		if (this.boundlessSIMetadata == null) {
			this.boundlessSIMetadata = new BoundlessServiceInstanceMetadata();
			boundlessSIMetadata.generateAndSetId();
		}
		
		String randomId = plan.getName() + "-" + generateRandom();
		
		// Pull the plan provided options (memory/instances/docker images) and load it as default
		for(BoundlessAppResource resource: this.boundlessSIMetadata.getAppResources()) {
			resource.loadDefaults(randomId, plan);
		}
		
		// If user has provided some overrides, use that on top
		this.boundlessSIMetadata.overrideResourceDefns(request.getParameters());
		
		// use the service instance's org & space guids
		boundlessSIMetadata.setOrgGuid(this.getOrgGuid());
		boundlessSIMetadata.setSpaceGuid(this.getSpaceGuid());
		
		log.debug("Service Instance metadata overridden on Creation: " + this);
	}
	
	public void update(UpdateServiceInstanceRequest request) {		
		this.boundlessSIMetadata.updateResourceDefns(request.getParameters());
		log.debug("Service Instance updated to: " + this);
	}
	
	@Override
	public String toString() {
		return "BoundlessServiceInstance [" + super.toString() + ", Metadata=" + boundlessSIMetadata + "]";
	}

	public BoundlessServiceInstanceMetadata getMetadata() {
		return boundlessSIMetadata;
	}

	public void setMetadata(BoundlessServiceInstanceMetadata bsiMetadata) {
		this.boundlessSIMetadata = bsiMetadata;
	}
	

}
