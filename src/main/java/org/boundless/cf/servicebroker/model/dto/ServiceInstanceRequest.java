package org.boundless.cf.servicebroker.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author krujos
 *
 */

public abstract class ServiceInstanceRequest {

	@JsonIgnore
	protected boolean acceptsIncomplete;
	
	public ServiceInstanceRequest(boolean acceptsIncomplete) {
		this.acceptsIncomplete = acceptsIncomplete;
	}

	public boolean hasAsyncClient() { 
		return acceptsIncomplete;
	}
	
}