package org.boundless.cf.servicebroker.service.boundless;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.repository.ServiceInstanceBindingRepository;
import org.boundless.cf.servicebroker.service.ServiceInstanceBindingService;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.boundless.cf.servicebroker.servicebroker.model.AppMetadata;
import org.boundless.cf.servicebroker.servicebroker.model.BoundlessServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BoundlessServiceInstanceBindingService implements
		ServiceInstanceBindingService {

	private static final Logger log = Logger
			.getLogger(BoundlessServiceInstanceBindingService.class);

	@Autowired
	BoundlessServiceInstanceService serviceInstanceService;

	@Autowired
	ServiceInstanceBindingRepository repository;

	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request)
			throws ServiceInstanceBindingExistsException,
			ServiceBrokerException {

		log.info("Incoming CreateServiceInstanceBindingRequest: " + request);
		
		String bindingId = request.getBindingId();
		if (bindingId == null) {
			throw new ServiceBrokerException("no bindingId in request.");
		}

		ServiceInstanceBinding sib = repository.findOne(bindingId);
		if (sib != null) {
			throw new ServiceInstanceBindingExistsException(sib);
		}

		String serviceInstanceId = request.getServiceInstanceId();
		BoundlessServiceInstance bsi = (BoundlessServiceInstance) serviceInstanceService
				.getServiceInstance(serviceInstanceId);

		if (bsi == null) {
			throw new ServiceBrokerException("service instance for binding: "
					+ bindingId + " is missing.");
		}

		// not supposed to happen per the spec, but better check...
		if (bsi.isInProgress()) {
			throw new ServiceBrokerException(
					"ServiceInstance operation is still in progress.");
		}

		serviceInstanceService.saveInstance(bsi);
		
		AppMetadata appMetadata = bsi.getAppMetadata();
		Map<String, String> credMap = new HashMap<String, String>();
		credMap.put("name", appMetadata.getApp());
		credMap.put("guid", appMetadata.getAppGuid());
		credMap.put("uri", appMetadata.getRouteName());
		credMap.put("org", appMetadata.getOrg());
		credMap.put("space", appMetadata.getSpace());
		credMap.put("image", appMetadata.getDockerImage());
		
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId,
				serviceInstanceId, bsi.getServiceId(), bsi.getPlanId(), credMap, null,
				request.getAppGuid());

		log.info("Saving ServiceInstanceBinding: " + binding);
		return repository.save(binding);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request)
			throws ServiceBrokerException {

		log.info("Incoming DeleteServiceInstanceBindingRequest: " + request);
		
		ServiceInstanceBinding binding = repository.findOne(request
				.getBindingId());

		if (binding == null) {
			throw new ServiceBrokerException("binding with id: "
					+ request.getBindingId() + " does not exist.");
		}

		repository.delete(binding);
		return binding;
	}
}