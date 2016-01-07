package org.boundless.cf.servicebroker.repository;

import org.boundless.cf.servicebroker.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface ServiceInstanceRepository extends
		CrudRepository<ServiceInstance, String> {

}
