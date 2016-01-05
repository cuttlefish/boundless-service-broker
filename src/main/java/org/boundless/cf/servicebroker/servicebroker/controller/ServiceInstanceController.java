package org.boundless.cf.servicebroker.servicebroker.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.service.CatalogService;
import org.boundless.cf.servicebroker.service.ServiceInstanceService;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceExistsException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.boundless.cf.servicebroker.servicebroker.model.CreateServiceInstanceRequest;
import org.boundless.cf.servicebroker.servicebroker.model.DeleteServiceInstanceRequest;
import org.boundless.cf.servicebroker.servicebroker.model.ErrorMessage;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceDefinition;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceLastOperation;
import org.boundless.cf.servicebroker.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * See: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * From: https://github.com/cloudfoundry-community/spring-boot-cf-service-broker/blob/master/src/main/java/org/cloudfoundry/community/servicebroker/controller/ServiceInstanceController.java
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger log = Logger.getLogger(ServiceInstanceController.class);
	
	private ServiceInstanceService service;
	private CatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceController(ServiceInstanceService service, CatalogService catalogService) {
 		this.service = service;
 		this.catalogService = catalogService;
 	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstance> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete,
			@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException, ServiceBrokerAsyncRequiredException {
		log.info("PUT: " + BASE_PATH + "/{instanceId}?accepts_incomplete=" + acceptsIncomplete 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId 
				+ " and request paylaod: " + request);
		
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		
		ServiceInstance instance = service.createServiceInstance(
				request.withServiceDefinition(svc).and().withServiceInstanceId(serviceInstanceId)
					.and().withAcceptsIncomplete(acceptsIncomplete));
		log.info("ServiceInstance Created: " + instance.getId());

		return new ResponseEntity<>(
				instance, instance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.CREATED);
		
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}/last_operation", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceInstanceLastOperation(
			@PathVariable("instanceId") String instanceId) {

		log.info("GET: " + BASE_PATH + "/{instanceId}/last_operation"
				+ ", getServiceInstance(), serviceInstanceId = " + instanceId);

		ServiceInstance instance = service.getServiceInstance(instanceId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (null == instance) {
			return new ResponseEntity<>("{}", headers, HttpStatus.GONE);
		}
		ServiceInstanceLastOperation lastOperation = instance.getLastOperation();
		log.info("ServiceInstance: " + instance.getId() + " is in " + lastOperation.getState() + " state. Details : " +lastOperation.getDescription());
		return new ResponseEntity<>(lastOperation, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId,
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete) 
					throws ServiceBrokerException, ServiceBrokerAsyncRequiredException {
		log.info( "DELETE: " + BASE_PATH + "/{instanceId}?accepts_incomplete=" + acceptsIncomplete 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = service.deleteServiceInstance(
				new DeleteServiceInstanceRequest(instanceId, serviceId, planId, acceptsIncomplete));
		
		if (instance == null) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
		
		log.info("ServiceInstance Deleted: " + instance.getId());
		return new ResponseEntity<>(instance,
				instance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServiceInstance(
			@PathVariable("instanceId") String instanceId,
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete,
			@Valid @RequestBody UpdateServiceInstanceRequest request) throws 
			ServiceInstanceUpdateNotSupportedException,
			ServiceInstanceDoesNotExistException, 
			ServiceBrokerException, ServiceBrokerAsyncRequiredException {
		log.info("UPDATE: " + BASE_PATH + "/{instanceId}?accepts_incomplete=" + acceptsIncomplete
				+ ", updateServiceInstanceBinding(), serviceInstanceId = "
				+ instanceId + ", planId = " + request.getPlanId() 
				+ " and request paylaod: " + request);
		ServiceInstance instance = service.updateServiceInstance(
				request.withInstanceId(instanceId).withAcceptsIncomplete(acceptsIncomplete));
		log.info("ServiceInstance updated: " + instance.getId());
		HttpStatus status = instance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK; 
		return new ResponseEntity<>("{}", status);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceDefinitionDoesNotExistException ex) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceUpdateNotSupportedException ex) {
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

}