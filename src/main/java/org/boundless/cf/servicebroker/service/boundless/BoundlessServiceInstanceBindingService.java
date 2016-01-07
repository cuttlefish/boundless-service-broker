package org.boundless.cf.servicebroker.service.boundless;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.boundless.cf.servicebroker.model.BoundlessAppMetadata;
import org.boundless.cf.servicebroker.model.BoundlessServiceInstance;
import org.boundless.cf.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.model.ServiceInstanceBinding;
import org.boundless.cf.servicebroker.repository.ServiceInstanceBindingRepository;
import org.boundless.cf.servicebroker.service.ServiceInstanceBindingService;
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
		
		BoundlessAppMetadata appMetadata = bsi.getAppMetadata();
		Map<String, String> credMap = new HashMap<String, String>();
		if (appMetadata != null) {
			credMap.put("geoserver_name", appMetadata.getGeoServerApp());
			credMap.put("geoserver_guid", appMetadata.getGeoServerAppGuid());
			credMap.put("geoserver_uri", appMetadata.getGeoServerRoute());
			credMap.put("geocache_name", appMetadata.getGeoCacheApp());
			credMap.put("geocache_guid", appMetadata.getGeoCacheAppGuid());
			credMap.put("geocache_uri", appMetadata.getGeoCacheRoute());
			credMap.put("org", appMetadata.getOrg());
			credMap.put("space", appMetadata.getSpace());
			credMap.put("geoserver_image", appMetadata.getGeoServerDockerImage());
			credMap.put("geocache_image", appMetadata.getGeoCacheDockerImage());
		}
		
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