package org.boundless.cf.servicebroker.servicebroker.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "service_instances")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ServiceInstance {
	
	protected static transient final Log log = LogFactory.getLog(ServiceDefinition.class);

	public static final String CREATE_REQUEST = "CREATE_REQUEST";
	public static final String UPDATE_REQUEST = "UPDATE_REQUEST";
	public static final String DELETE_REQUEST = "DELETE_REQUEST";
	

	@Id
	protected String id;

	@JsonSerialize
	@JsonProperty("service_id")
	@Column(nullable = false)
	protected String serviceId;

	@JsonSerialize
	@JsonProperty("plan_id")
	@Column(nullable = false)
	protected String planId;

	@JsonSerialize
	@JsonProperty("organization_guid")
	@Column(nullable = false)
	protected String orgGuid;

	@JsonSerialize
	@JsonProperty("space_guid")
	@Column(nullable = false)
	protected String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("dashboard_url")
	@Column(nullable = true)
	protected String dashboardUrl;
	
	@JsonSerialize
	@JsonProperty("last_operation")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	protected ServiceInstanceLastOperation lastOperation; 

	// In Boundless, the service instance is actually tied to an app pushed to CF
	//@JsonSerialize
	//@JsonProperty("cf_docker_app")
	//@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	//private CFDockerApp cfApp; 


	// any "parameters" tags/key-value pairs    
	//@ElementCollection(fetch = FetchType.LAZY)
	//@MapKeyColumn(name="name")
    //@Column(name="value")
    //@CollectionTable(name="service_instance_params", joinColumns=@JoinColumn(name="service_instance_params_id"))
	@Transient
	protected Map<String,Object> parameters = new HashMap<String,Object>();
		
	@JsonIgnore
	protected boolean async = true;
	
	@JsonIgnore
	protected boolean acceptsIncomplete = true;
	
	@JsonSerialize
	@Column(nullable = true)
	private String currentOperation;

	//@JsonIgnore
	@Column(nullable = false)
	protected Date creationTime = new Date(); 
	
	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public ServiceInstance() { }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ServiceInstanceLastOperation getLastOperation() {
		
		// FIXME: HACK To get over the Creation Status update to Succeeded
		/*
		long currentTime = System.currentTimeMillis();
		if (currentTime - this.creationTime.getTime() > 100000) {
			lastOperation.setState(OperationState.SUCCEEDED); 
		}
		*/
		return lastOperation;
	}

	public void setLastOperation(ServiceInstanceLastOperation lastOperation) {
		this.lastOperation = lastOperation;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
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

	public boolean isAsync() {
		return async;
	}
	
	public ServiceInstance withAsync(boolean async) {
		this.async = async;
		return this;
	}
	
	public ServiceInstance withAacceptsIncomplete(boolean acceptsIncomplete) {
		this.acceptsIncomplete = acceptsIncomplete;
		return this;
	}
	
	/*
	public boolean isCurrentOperationCreate() {
		return CREATE_REQUEST.equals(currentOperation);
	}

	public boolean isCurrentOperationUpdate() {
		return UPDATE_REQUEST.equals(currentOperation);
	}
	
	public boolean isCurrentOperationDelete() {
		return DELETE_REQUEST.equals(currentOperation);
	}
	*/

	public String getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(String currentState) {
		this.currentOperation = currentState;
	}
	
	public boolean isInProgress() {
		log.info("Checking if in Progress: " + getLastOperation());

		if (getLastOperation() == null
				|| getLastOperation().getState() == null) {
			return false;
		}

		return getLastOperation().getState().equals(
				OperationState.IN_PROGRESS);
	}
	
	public boolean isCurrentOperationSuccessful() {
		log.info("Checking if successful: " + getLastOperation());
		if (getLastOperation() == null
				|| getLastOperation().getState() == null) {
			return false;
		}
		
		return getLastOperation().getState().equals(
				OperationState.SUCCEEDED);
	}

	/*
	@JsonIgnore
	public AppMetadata getAppMetadata() {
		return appMetadata;
	}

	@JsonProperty
	public void setAppMetadata(AppMetadata appMetadata) {
		this.appMetadata = appMetadata;
	}
	*/
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceInstance other = (ServiceInstance) obj;
		if (async != other.async)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastOperation == null) {
			if (other.lastOperation != null)
				return false;
		} else if (!lastOperation.equals(other.lastOperation))
			return false;
		if (orgGuid == null) {
			if (other.orgGuid != null)
				return false;
		} else if (!orgGuid.equals(other.orgGuid))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (planId == null) {
			if (other.planId != null)
				return false;
		} else if (!planId.equals(other.planId))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (spaceGuid == null) {
			if (other.spaceGuid != null)
				return false;
		} else if (!spaceGuid.equals(other.spaceGuid))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, planId, orgGuid, spaceGuid, acceptsIncomplete, parameters);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		log.info("Parameters:");
		for(String key: parameters.keySet()) {
			log.info("[" + key + ": " + parameters.get(key) + "]");
		}
	}
	
	/*
	public <T> T getParameters(Class<T> cls) {
		try {
			T bean = cls.newInstance();
			BeanUtils.populate(bean, parameters);
			return bean;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error mapping parameters to class of type " + cls.getName());
		}
	}
	*/	
	
	public void update(ServiceInstance from) {
		if (from == null)
			return;
		
		if (from.orgGuid != null) {
			this.orgGuid = from.orgGuid;
		}
		
		if (from.planId != null) {
			this.planId = from.planId;
		}
		
		if (from.spaceGuid != null ) {
			this.spaceGuid = from.spaceGuid;
		}
		
		if (from.serviceId != null) {
			serviceId = from.serviceId;
		}
	
		if (from.lastOperation != null) {
			this.lastOperation = from.lastOperation;
		}
	
		if (from.parameters != null) {
			this.parameters = from.parameters;
		}		
	}

	public ServiceInstance withLastOperation(ServiceInstanceLastOperation lastOperation) {
		this.lastOperation = lastOperation;
		return this;
	}

	public ServiceInstance withDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
		return this;
	}

	/**
	 * Create a ServiceInstance from a create request. If fields 
	 * are not present in the request they will remain null in the  
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(CreateServiceInstanceRequest request) {
		this.serviceId = request.getServiceDefinitionId();
		this.planId = request.getPlanId();
		this.orgGuid = request.getOrganizationGuid();
		this.spaceGuid = request.getSpaceGuid();
		this.id = request.getServiceInstanceId();
		this.lastOperation = new ServiceInstanceLastOperation("Provisioning", OperationState.IN_PROGRESS);
		this.parameters = request.getParameters();
	}
	
	/**
	 * Create a ServiceInstance from a delete request. If fields 
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(DeleteServiceInstanceRequest request) {
		this.id = request.getServiceInstanceId();
		this.planId = request.getPlanId();
		this.serviceId = request.getServiceId();
		this.lastOperation = new ServiceInstanceLastOperation("Deprovisioning", OperationState.IN_PROGRESS);

	}
	
	/**
	 * Create a service instance from an update request. If fields
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(UpdateServiceInstanceRequest request) {
		this.id = request.getServiceInstanceId();
		this.planId = request.getPlanId();
		this.lastOperation = new ServiceInstanceLastOperation("Updating", OperationState.IN_PROGRESS);
	}
}
