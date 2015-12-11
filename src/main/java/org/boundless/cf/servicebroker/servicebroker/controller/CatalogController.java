package org.boundless.cf.servicebroker.servicebroker.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boundless.cf.servicebroker.cfutils.CFAppManager;
import org.boundless.cf.servicebroker.repository.CredentialsRepository;
import org.boundless.cf.servicebroker.repository.PlanRepository;
import org.boundless.cf.servicebroker.repository.ServiceInstanceBindingRepository;
import org.boundless.cf.servicebroker.repository.ServiceInstanceRepository;
import org.boundless.cf.servicebroker.repository.ServiceDefinitionnRepository;
import org.boundless.cf.servicebroker.service.CatalogService;
import org.boundless.cf.servicebroker.servicebroker.model.Catalog;
import org.boundless.cf.servicebroker.servicebroker.model.Credentials;
import org.boundless.cf.servicebroker.servicebroker.model.OperationState;
import org.boundless.cf.servicebroker.servicebroker.model.Plan;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceDefinition;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceBinding;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceLastOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class CatalogController extends BaseController  {

	public static final String BASE_PATH = "/v2/catalog";
	
	private static final Log log = LogFactory.getLog(CatalogController.class);

	private CatalogService service;
	
	@Autowired 
	public CatalogController(CatalogService service) {
		this.service = service;
	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
	public @ResponseBody Catalog getCatalog() {
		log.debug("GET: " + BASE_PATH + ", getCatalog()");
		return service.getCatalog();
	}
	
	/*
	
	@Autowired
	ServiceRepository serviceRepo;

	@Autowired
	PlanRepository planRepo;
	
	@Autowired
	CredentialsRepository credentialRepo;

	@Autowired
	ServiceInstanceRepository serviceInstanceRepo;

	@Autowired
	ServiceBindingRepository serviceBindingRepo;

	@Autowired
	CFAppManager cfAppManager;
	
	@RequestMapping("/v2/catalog")
	public Map<String, Iterable<ServiceDefinition>> catalog() {
		Map<String, Iterable<ServiceDefinition>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());

		log.info("Catalog content: " + wrapper);
		return wrapper;
	}

	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> create(@PathVariable("id") String id,
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete,
			@RequestBody ServiceInstance serviceInstance) {
		
		log.info("PUT: /v2/service_instances/" + id + " with payload: " + serviceInstance);
		serviceInstance.setId(id);
		
		serviceInstance.setLastOperation(new ServiceInstanceLastOperation("Provisioning", OperationState.IN_PROGRESS));    	
    	serviceInstance.createApp(cfAppManager);
    	
		boolean exists = serviceInstanceRepo.exists(id);
		if (exists) {
			ServiceInstance existing = serviceInstanceRepo.findOne(id);
			if (existing.equals(serviceInstance)) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
			}
		} else {
			serviceInstanceRepo.save(serviceInstance);
			return new ResponseEntity<>(
					serviceInstance, serviceInstance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.CREATED);
		}

	}
	
	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<String> update(@PathVariable("id") String id,
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete,
			@RequestBody ServiceInstance updateInstance) {
		
		log.info("PATCH: /v2/service_instances/" + id  + " with payload: " + updateInstance);
		updateInstance.setId(id);

		boolean exists = serviceInstanceRepo.exists(id);
		if (!exists)
			return new ResponseEntity<>("{ Service instance not found, Create first !!}", HttpStatus.NOT_FOUND);
	
		ServiceInstance serviceInstance = serviceInstanceRepo.findOne(id);
		serviceInstance.update(updateInstance);
		
		serviceInstance.setLastOperation(new ServiceInstanceLastOperation("Updating", OperationState.IN_PROGRESS));
		serviceInstance.updateApp(cfAppManager);
		
		serviceInstanceRepo.save(serviceInstance);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	
	}


	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") String id,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId,
			@RequestParam(value="accepts_incomplete", required=false) boolean acceptsIncomplete) {
		
		log.info("DELETE: /v2/service_instances/" + id + " with request params, service_id: " + serviceId + " and plan_id: " + planId);
		boolean exists = serviceRepo.exists(id);
		
		if (exists) {
			
			ServiceInstance serviceInstance = serviceInstanceRepo.findOne(id);
			serviceInstance.setLastOperation(new ServiceInstanceLastOperation("Deprovisioning", OperationState.IN_PROGRESS));
			serviceInstance.deleteApp(cfAppManager);
			
			serviceInstanceRepo.delete(id);
			// Actualservice.delete(id);
			return new ResponseEntity<>(serviceInstance,
					serviceInstance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}


	@RequestMapping(value = "/v2/service_instances/{instanceId}/last_operation", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceInstanceLastOperation(
			@PathVariable("instanceId") String instanceId) {

		log.info("GET: " + "/v2/service_instances/" + instanceId + "/last_operation");

		ServiceInstance instance = serviceInstanceRepo.findOne(instanceId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (null == instance) {
			return new ResponseEntity<>("{}", headers, HttpStatus.GONE);
		}
		
		ServiceInstanceLastOperation lastOperation = instance.getLastOperation();
		log.info("ServiceInstance: " + instance.getId() + "is in " + lastOperation.getState() + " state. Details : " +lastOperation.getDescription());
		return new ResponseEntity<>(lastOperation, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> create(
			@PathVariable("instanceId") String instanceId,
			@PathVariable("id") String id,
			@RequestBody ServiceInstanceBinding serviceBinding) {

		log.info("PUT: /v2/service_instances/" + instanceId + "/service_bindings/" + id + " with payload: " + serviceBinding);
		if (!serviceInstanceRepo.exists(instanceId)) {
			System.out.println("Instance does not exists..");
			return new ResponseEntity<Object>(
					"{\"description\": \"Service Instance with id "
							+ instanceId + " not found\"}",
					HttpStatus.BAD_REQUEST);

		}
		
		ServiceInstance serviceInstance = serviceInstanceRepo.findOne(instanceId);		
		ServiceDefinition underlyingService = serviceRepo.findOne(serviceInstance.getServiceId());		
		Plan underlyingPlan = planRepo.findOne(serviceInstance.getPlanId());
		
		serviceBinding.setId(id);
		serviceBinding.setInstanceId(instanceId);

		boolean exists = serviceBindingRepo.exists(id);
		if (exists) {
			ServiceInstanceBinding existing = serviceBindingRepo.findOne(id);
			if (existing.equals(serviceBinding)) {
				return new ResponseEntity<Object>(
						wrapCredentials(existing.getCredentials()),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<Object>("{}", HttpStatus.CONFLICT);
			}
		} else {
			
			Credentials creds = underlyingPlan.getCredentials();
			serviceBinding.setCredentials(creds);
			serviceBindingRepo.save(serviceBinding);
			return new ResponseEntity<Object>(wrapCredentials(creds),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteBinding(
			@PathVariable("instanceId") String instanceId,
			@PathVariable("id") String id,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) {
		
		log.info("DELETE: /v2/service_instances/" + instanceId + "/service_bindings/" + id + " with request params, service_id: " + serviceId + " and plan_id: " + planId);
		
		boolean exists = serviceBindingRepo.exists(id);

		if (exists) {
			serviceBindingRepo.delete(id);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}
	
	private Map<String, Object> wrapCredentials(Credentials credentials) {
		Map<String, Object> wrapper = new HashMap<>();
		wrapper.put("credentials", credentials);
		return wrapper;
	}
	*/

	
}
