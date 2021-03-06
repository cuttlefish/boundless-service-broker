package org.boundless.cf.servicebroker.exception;

import org.boundless.cf.servicebroker.model.ServiceInstanceBinding;

/**
 * Thrown when a duplicate request to bind to a service instance is 
 * received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceBindingExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ServiceInstanceBindingExistsException(ServiceInstanceBinding binding) {
		super("ServiceInstanceBinding already exists: serviceInstanceBinding.id = "
				+ binding.getId()
				+ ", serviceInstance.id = " + binding.getInstanceId());
	}

}