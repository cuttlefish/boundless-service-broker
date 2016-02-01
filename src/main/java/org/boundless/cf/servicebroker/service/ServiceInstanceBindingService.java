package org.boundless.cf.servicebroker.service;

import org.boundless.cf.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.boundless.cf.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.boundless.cf.servicebroker.model.ServiceInstanceBinding;
import org.boundless.cf.servicebroker.model.dto.CreateServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.model.dto.DeleteServiceInstanceBindingRequest;


/**
 * Handles bindings to service instances.
 * 
 * @author sgreenberg@gopivotal.com
 */
public interface ServiceInstanceBindingService {

	/**
	 * Create a new binding to a service instance.
	 * @param createServiceInstanceBindingRequest containing parameters sent from Cloud Controller
	 * @return The newly created ServiceInstanceBinding
	 * @throws ServiceInstanceBindingExistsException if the same binding already exists
	 * @throws ServiceBrokerException on internal failure
	 */
	ServiceInstanceBinding createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest createServiceInstanceBindingRequest)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException;

	/**
	 * Delete the service instance binding. If a binding doesn't exist, 
	 * return null.
	 * @param deleteServiceInstanceBindingRequest containing parameters sent from Cloud Controller
     * @return The deleted ServiceInstanceBinding or null if one does not exist
     * @throws ServiceBrokerException on internal failure
	 * @throws ServiceBrokerAsyncRequiredException if we must use an async comparable cloud controller
	 */
	ServiceInstanceBinding deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest) 
	        throws ServiceBrokerException, ServiceBrokerAsyncRequiredException;
	
}