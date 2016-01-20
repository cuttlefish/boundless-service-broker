package org.boundless.cf.servicebroker.controller;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.model.ServiceDefinition;
import org.boundless.cf.servicebroker.repository.BoundlessServiceInstanceMetadataRepository;
import org.boundless.cf.servicebroker.repository.BoundlessServiceInstanceRepository;
import org.boundless.cf.servicebroker.repository.PlanRepository;
import org.boundless.cf.servicebroker.repository.ServiceDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	private static final Logger log = Logger.getLogger(CatalogController.class);
	
	@Autowired
	private PlanRepository planRepo;

	@Autowired
	private ServiceDefinitionRepository serviceRepo;
	
	@Autowired
	private BoundlessServiceInstanceRepository serviceInstancesRepo;
	
	@Autowired
	private BoundlessServiceInstanceMetadataRepository serviceInstancesMetadataRepo;
	
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns(
		@PathVariable("serviceIdOrName") String serviceIdOrName) {
		
		Optional<ServiceDefinition> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
		
		ServiceDefinition existingService = services.get();
		return new ResponseEntity<>(existingService.getPlans(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns() {
		
		return new ResponseEntity<>(planRepo.findAll(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/serviceInstances", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServiceInstances() {
		
		serviceInstancesRepo.findAll().forEach(System.out::println);;
		return new ResponseEntity<>(serviceInstancesRepo.findAll(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/serviceInstancesMetadata", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServiceInstancesMetadata() {
		
		return new ResponseEntity<>(serviceInstancesMetadataRepo.findAll(), HttpStatus.OK);		
	}


}
