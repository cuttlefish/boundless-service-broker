package org.boundless.cf.servicebroker.servicebroker.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "serviceinstance_lastoperation")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer"})
@JsonAutoDetect
public class ServiceInstanceLastOperation {	
	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
	
	@JsonSerialize
	private String description;
	
	private OperationState state; 
	
	public ServiceInstanceLastOperation() {	}
	
	public ServiceInstanceLastOperation(
			final String description, 
			final OperationState operationState)  {
		setDescription(description); 
		this.state = operationState;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	private void setDescription(String description) {
		this.description = description;
	}	
	
	@JsonIgnore
	public OperationState getOprnState() {
		return state;
	}
	
	@JsonSerialize
	public String getState() {
		switch (state) { 
		case IN_PROGRESS: 
			return "in progress";
		case SUCCEEDED:
			return "succeeded"; 
		case FAILED: 
			return "failed";
		};
		assert(false);
		return "internal error";
	}
	
	@JsonSerialize
	public void setState(OperationState state) { 
		this.state = state;
	}
	
	@JsonSerialize
	public void setState(String state) { 
		switch(state) { 
		case "in progress": 
			this.state = OperationState.IN_PROGRESS;
			break;
		case "succeeded": 
			this.state = OperationState.SUCCEEDED; 
			break; 
		case "failed":
			this.state = OperationState.FAILED;
			break; 
		default:
			assert(false);
			break;
		}
	}

	@Override
	public String toString() {
		return "ServiceInstanceLastOperation [id=" + id + ", description="
				+ description + ", state=" + state + "]";
	}
	
	
}