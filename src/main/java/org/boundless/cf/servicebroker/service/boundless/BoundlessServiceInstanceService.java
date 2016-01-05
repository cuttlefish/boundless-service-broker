package org.boundless.cf.servicebroker.service.boundless;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.cfutils.CFAppManager;
import org.boundless.cf.servicebroker.repository.BoundlessServiceInstanceRepository;
import org.boundless.cf.servicebroker.repository.CFDockerAppRepository;
import org.boundless.cf.servicebroker.service.CatalogService;
import org.boundless.cf.servicebroker.service.ServiceInstanceService;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceExistsException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.boundless.cf.servicebroker.servicebroker.model.AppMetadata;
import org.boundless.cf.servicebroker.servicebroker.model.BoundlessServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.CreateServiceInstanceRequest;
import org.boundless.cf.servicebroker.servicebroker.model.DeleteServiceInstanceRequest;
import org.boundless.cf.servicebroker.servicebroker.model.OperationState;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceDefinition;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceLastOperation;
import org.boundless.cf.servicebroker.servicebroker.model.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoundlessServiceInstanceService implements ServiceInstanceService {

	private static final Logger log = Logger
			.getLogger(BoundlessServiceInstanceService.class);


	@Autowired
	CFAppManager appManager;
	
	@Autowired
	CatalogService catalogService;

	@Autowired
	BoundlessServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	CFDockerAppRepository cfAppRepository;


	@Override
	public ServiceInstance getServiceInstance(String id) {

		if (id == null || getInstance(id) == null) {
			log.warn("service instance with id: " + id + " not found!");
			return null;
		}

		BoundlessServiceInstance instance = getInstance(id);

		// check the last operation
		ServiceInstanceLastOperation silo = instance
				.getLastOperation();
		if (silo == null || silo.getState() == null) {
			log.error("ServiceInstance: " + id + " has no last operation.");
			deleteInstance(instance);
			return null;
		}

		// if the instance is not in progress just return it.
		if (!instance.isInProgress()) {
			return instance;
		}

		// if still in progress, let's check up on things...
		String currentRequestId = silo.getDescription();
		if (currentRequestId == null) {
			log.error("ServiceInstance: " + id + " last operation has no id.");
			deleteInstance(instance);
			return null;
		}

		String state = instance.getLastOperation().getState();
		log.info("service instance id: " + id + " request id: "
				+ currentRequestId + " is in state: " + state);

		log.info("checking on status of request id: " + currentRequestId);
		
		
		// Check the Status of the app push
		ServiceInstanceLastOperation status = null;
		
		// Continue with existing state till we get api support
		//instance.withLastOperation(status);

		// if this is a delete request and was successful, remove the instance
		if (instance.isCurrentOperationSuccessful()
				&& instance.getCurrentOperation().equals(ServiceInstance.DELETE_REQUEST)) {
			deleteInstance(instance);
		}

		// otherwise save the instance with the new last operation
		return saveInstance(instance);
	}

	@Override
	public ServiceInstance createServiceInstance(
			CreateServiceInstanceRequest request)
			throws ServiceInstanceExistsException, ServiceBrokerException {

		if (request == null || request.getServiceDefinitionId() == null) {
			throw new ServiceBrokerException(
					"invalid CreateServiceInstanceRequest object.");
		}

		if (request.getServiceInstanceId() != null
				&& getInstance(request.getServiceInstanceId()) != null) {
			throw new ServiceInstanceExistsException(serviceInstanceRepository.findOne(request
					.getServiceInstanceId()));
		}

		ServiceDefinition sd = catalogService.getServiceDefinition(request
				.getServiceDefinitionId());

		if (sd == null) {
			throw new ServiceBrokerException(
					"Unable to find service definition with id: "
							+ request.getServiceDefinitionId());
		}

		log.info("creating service instance: " + request.getServiceInstanceId()
				+ " service definition: " + request.getServiceDefinitionId());

		
		BoundlessServiceInstance serviceInstance = new BoundlessServiceInstance(request);	
		serviceInstance.setCurrentOperation(ServiceInstance.CREATE_REQUEST);
		serviceInstance.setLastOperation(new ServiceInstanceLastOperation("Provisioning", OperationState.IN_PROGRESS));
    	serviceInstance = saveInstance(serviceInstance);
    	
    	createApp(serviceInstance);
    	serviceInstance = saveInstance(serviceInstance);

		log.info("Registered service instance: "
				+ serviceInstance);

		return serviceInstance;
	}

	@Override
	public ServiceInstance deleteServiceInstance(
			DeleteServiceInstanceRequest request) throws ServiceBrokerException {

		if (request == null || request.getServiceInstanceId() == null) {
			throw new ServiceBrokerException(
					"invalid DeleteServiceInstanceRequest object.");
		}

		BoundlessServiceInstance serviceInstance = getInstance(request.getServiceInstanceId());
		if (serviceInstance == null) {
			throw new ServiceBrokerException("Service instance: "
					+ request.getServiceInstanceId() + " not found.");
		}

		serviceInstance.setCurrentOperation(ServiceInstance.DELETE_REQUEST);
		serviceInstance.setLastOperation(new ServiceInstanceLastOperation("Deprovisioning", OperationState.IN_PROGRESS));
		serviceInstance = saveInstance(serviceInstance);
		log.info("deleting service instance: " + request.getServiceInstanceId() + ", full ServiceInstance details: " + serviceInstance);

		serviceInstance = deleteInstance(serviceInstance);

		log.info("Unregistered service instance: "
				+ serviceInstance);

		return serviceInstance;
	}

	@Override
	public ServiceInstance updateServiceInstance(
			UpdateServiceInstanceRequest request)
			throws ServiceInstanceUpdateNotSupportedException,
			ServiceBrokerException, ServiceInstanceDoesNotExistException {

		BoundlessServiceInstance existingInstance = getInstance(request.getServiceInstanceId());
		BoundlessServiceInstance updateToInstance = new BoundlessServiceInstance(request);
		
		if (existingInstance == null || existingInstance.getId() == null) {
			return null;
		}
		existingInstance.update(updateToInstance);
		log.info("Updated service instance to: "
				+ existingInstance);
		
		// First persist the state so if any calls into check the state it can show as being deleted...
		existingInstance.setCurrentOperation(ServiceInstance.UPDATE_REQUEST);
		existingInstance.setLastOperation(new ServiceInstanceLastOperation("Updating", OperationState.IN_PROGRESS));
		existingInstance = saveInstance(existingInstance);	
		
		this.updateApp(existingInstance);
		existingInstance.getLastOperation().setState(OperationState.SUCCEEDED);
		
		AppMetadata cfApp = existingInstance.getAppMetadata();
		if (cfApp != null) {
			cfAppRepository.save(cfApp);	
		}

		existingInstance = saveInstance(existingInstance);
		log.info("Updated service instance: "
				+ existingInstance);
		return existingInstance;
	}

	private BoundlessServiceInstance getInstance(String id) {
		if (id == null) {
			return null;
		}
		return serviceInstanceRepository.findOne(id);
	}

	BoundlessServiceInstance deleteInstance(BoundlessServiceInstance instance) {
		if (instance == null || instance.getId() == null) {
			return null;
		}
		
		// First persist the state so if any calls into check the state it can show as being deleted...
		instance.setCurrentOperation(BoundlessServiceInstance.DELETE_REQUEST);
		instance.setLastOperation(new ServiceInstanceLastOperation("Deprovisioning", OperationState.IN_PROGRESS));
		serviceInstanceRepository.save(instance);
		
		log.info("Starting deletion of service instance: "
				+ instance);
		
		this.deleteApp(instance);    	
		instance.getLastOperation().setState(OperationState.SUCCEEDED);
		saveInstance(instance);
		
		if (instance.isCurrentOperationSuccessful()
				&& instance.getCurrentOperation().equals(ServiceInstance.DELETE_REQUEST)) {
			
			AppMetadata cfApp = instance.getAppMetadata();
			instance.setAppMetadata(null);
			
			if (cfApp != null) {
				cfAppRepository.delete(cfApp);	
			}
			
			serviceInstanceRepository.delete(instance.getId());
		}
		
		log.info("Done deletion of service instance: "
				+ instance);
		return instance;
	}

	BoundlessServiceInstance saveInstance(BoundlessServiceInstance instance) {
		return serviceInstanceRepository.save(instance);
	}	
	
	public void createApp(BoundlessServiceInstance serviceInstance) {
		
		AppMetadata cfApp = serviceInstance.getAppMetadata();
    	SpringCloudFoundryClient cfClient = appManager.getCfClient();
	 	String orgId = appManager.orgId( cfApp.getOrg());
    	String spaceId = appManager.spaceId( orgId, cfApp.getSpace());
    	
    	cfApp.setOrgGuid(orgId);
    	cfApp.setSpaceGuid(spaceId);
    	
    	serviceInstance.setAppMetadata(cfApp);
    	log.info("App Metadata before push: " + cfApp);
    	
    	appManager.pushApp(cfApp); 
    	serviceInstance.getLastOperation().setState(OperationState.SUCCEEDED);    	
	}

	public void updateApp(BoundlessServiceInstance serviceInstance) {
		
		AppMetadata cfApp = serviceInstance.getAppMetadata();
		
		if (cfApp == null)
			return;
		
    	SpringCloudFoundryClient cfClient = appManager.getCfClient();
    	
    	// Retrieve the current guids for org, space, app
	 	String orgId = appManager.orgId( cfApp.getOrg());
    	String spaceId = appManager.spaceId( orgId, cfApp.getSpace());
    	String appId = appManager.appId( orgId, spaceId, cfApp.getApp());    	
    	
    	cfApp.setOrgGuid(orgId);
    	cfApp.setSpaceGuid(spaceId);
    	cfApp.setAppGuid(appId);

    	serviceInstance.setAppMetadata(cfApp);    	
    	appManager.pushApp(cfApp);
	}
	
	public void deleteApp(BoundlessServiceInstance serviceInstance) {
		
		AppMetadata cfApp = serviceInstance.getAppMetadata();
		appManager.deleteApp(cfApp);
		serviceInstance.setAppMetadata(null);
		cfAppRepository.delete(cfApp);	
		
		serviceInstance.getLastOperation().setState(OperationState.SUCCEEDED);
	}
}