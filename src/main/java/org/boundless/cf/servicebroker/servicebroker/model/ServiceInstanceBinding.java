package org.boundless.cf.servicebroker.servicebroker.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "service_bindings")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstanceBinding {

	@Id
	private String id;

	@Column(nullable = false)
	private String instanceId;

	@JsonSerialize
	@JsonProperty("service_id")
	@Column(nullable = false)
	private String serviceId;

	@JsonSerialize
	@JsonProperty("plan_id")
	@Column(nullable = false)
	private String planId;

	@JsonSerialize
	@JsonProperty("app_guid")
	@Column(nullable = false)
	private String appGuid;

	@OneToOne// (orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "service_binding_id")
	private AppMetadata appMetadata;
	
	@JsonSerialize
	@JsonProperty("syslog_drain")
	@Column(nullable = true)
	private String syslogDrainUrl;
	
	public ServiceInstanceBinding() { }
	
	public ServiceInstanceBinding(String id, 
			String serviceInstanceId, 
			Map<String,Object> credentials,
			String syslogDrainUrl, String appGuid) {
		this.id = id;
		this.instanceId = serviceInstanceId;
		setCredentials(credentials);
		this.syslogDrainUrl = syslogDrainUrl;
		this.appGuid = appGuid;
	}
	
	public ServiceInstanceBinding(String id, 
			String serviceInstanceId, 
			AppMetadata appMetadata,
			String syslogDrainUrl, String appGuid) {
		this.id = id;
		this.instanceId = serviceInstanceId;
		this.appMetadata = appMetadata;
		this.syslogDrainUrl = syslogDrainUrl;
		this.appGuid = appGuid;
	}
	
	

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ServiceInstanceBinding that = (ServiceInstanceBinding) o;

		if (!appGuid.equals(that.appGuid))
			return false;
		if (!id.equals(that.id))
			return false;
		if (!instanceId.equals(that.instanceId))
			return false;
		if (!planId.equals(that.planId))
			return false;
		if (!serviceId.equals(that.serviceId))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + instanceId.hashCode();
		result = 31 * result + serviceId.hashCode();
		result = 31 * result + planId.hashCode();
		result = 31 * result + appGuid.hashCode();
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	public AppMetadata getCredentials() {
		return appMetadata;
	}

	public void setCredentials(AppMetadata credentials) {
		this.appMetadata = credentials;
	}
	
	private void setCredentials(Map<String, Object> appMetadataMap) {
		this.appMetadata = new AppMetadata();
		
		if (appMetadataMap == null) {
			return;
		}
		
		for(String key: appMetadataMap.keySet()) {
			this.appMetadata.setMapping(key, appMetadataMap.get(key));
		}
	}

}
