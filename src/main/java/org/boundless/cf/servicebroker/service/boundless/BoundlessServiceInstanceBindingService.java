package org.boundless.cf.servicebroker.service.boundless;

import org.boundless.cf.servicebroker.repository.ServiceInstanceBindingRepository;
import org.boundless.cf.servicebroker.service.ServiceInstanceBindingService;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerException;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.boundless.cf.servicebroker.servicebroker.model.BoundlessServiceInstance;
import org.boundless.cf.servicebroker.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceInstanceBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BoundlessServiceInstanceBindingService implements
		ServiceInstanceBindingService {

	@Autowired
	BoundlessServiceInstanceService serviceInstanceService;

	@Autowired
	ServiceInstanceBindingRepository repository;

	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request)
			throws ServiceInstanceBindingExistsException,
			ServiceBrokerException {

		String bindingId = request.getBindingId();
		if (bindingId == null) {
			throw new ServiceBrokerException("no bindingId in request.");
		}

		ServiceInstanceBinding sib = repository.findOne(bindingId);
		if (sib != null) {
			throw new ServiceInstanceBindingExistsException(sib);
		}

		String serviceInstanceId = request.getServiceInstanceId();
		BoundlessServiceInstance si = (BoundlessServiceInstance) serviceInstanceService
				.getServiceInstance(serviceInstanceId);

		if (si == null) {
			throw new ServiceBrokerException("service instance for binding: "
					+ bindingId + " is missing.");
		}

		// not supposed to happen per the spec, but better check...
		if (si.isInProgress()) {
			throw new ServiceBrokerException(
					"ServiceInstance operation is still in progress.");
		}

		serviceInstanceService.saveInstance(si);
		
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId,
				serviceInstanceId, si.getAppMetadata(), null,
				request.getAppGuid());

		return repository.save(binding);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request)
			throws ServiceBrokerException {

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