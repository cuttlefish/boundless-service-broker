package org.boundless.cf.servicebroker.exception;

import org.boundless.cf.servicebroker.model.ServiceInstance;

/**
 * Thrown when a duplicate service instance creation request is
 * received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ServiceInstanceExistsException(ServiceInstance instance) {
		super("ServiceInstance with the given ID already exists: " +
				"ServiceInstance.id = " + instance.getId() +
				", Service.id = " + instance.getServiceId());
	}

}