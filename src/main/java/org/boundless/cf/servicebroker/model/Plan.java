package org.boundless.cf.servicebroker.model;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "plans")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "service", "serviceId" })
public class Plan {
	
	private static final Logger log = Logger.getLogger(Plan.class);

	@Id
	private String id;

	private String name;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="service_id", insertable = true, updatable = false)
	// Mark insertable false for compound keys, shared primary key, cascaded key
	private ServiceDefinition service;

	@Column(nullable = false)
	private String description;
	
	@JsonProperty("free")
	@Column(nullable = true)
	private Boolean isFree = Boolean.TRUE;

	@JsonProperty("metadata")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private PlanMetadata metadata;
	
	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name = "planconfig_id", insertable=true, updatable=true, nullable=true, unique=true)
	private PlanConfig planConfig;

	//@JsonIgnore // FIX ME - remove comments
	public PlanConfig getPlanConfig() {
		return planConfig;
	}

	@JsonProperty
	public void setPlanConfig(PlanConfig planConfig) {
		this.planConfig = planConfig;
	}

	public boolean isFree() {
		return isFree.booleanValue();
	}

	public void setFree(boolean free) {
		if (isFree == null)
			isFree = true;
		this.isFree = free;
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

	public synchronized String getId () { 
		if (id == null)
			generateAndSetId();
		return id; 
	}

	public ServiceDefinition getService () { return service; }
	
	public void setService (ServiceDefinition  service) { 
		this.service = service; 
	}
	
	public String getName () { 
		return name; 
	}

	public void setName (String name) { 
		this.name = name; 
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descrp) {
		this.description = descrp;
	}
	
	public PlanMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(PlanMetadata planMetadata) {
		this.metadata = planMetadata;
	}
	
	public void copy(Plan copyPlan) {
		PlanMetadata metadata = copyPlan.getMetadata();
		if (metadata != null) {
			this.metadata = metadata;
		}
		
		String descrp = copyPlan.getDescription();
		if (descrp != null) {
			this.description = descrp;
		}
		
		this.isFree = copyPlan.isFree;		
	}

	@Override
	public String toString() {
		return "Plan [id=" + id + ", name=" + name 
				+ ", description=" + description + ", isFree=" + isFree
				+ ", metadata=" + metadata + ", planConfig="
				+ planConfig + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Plan other = (Plan) obj;
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void update(Plan from) {
		if (from == null)
			return;
		
		if (from.name != null) {
			this.name = from.name;
		}
		
		if (from.description != null) {
			this.description = from.description;
		}
		
		if (from.isFree != this.isFree ) {
			this.isFree = from.isFree;
		}
		
		if (from.metadata != null) {
			this.metadata.update(from.metadata);
		}
		
		if (from.planConfig != null) {
			this.planConfig.update(from.planConfig);
		}
	}
	
	
}
