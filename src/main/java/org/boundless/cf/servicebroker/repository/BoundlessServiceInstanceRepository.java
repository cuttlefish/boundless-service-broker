package org.boundless.cf.servicebroker.repository;

import org.boundless.cf.servicebroker.model.BoundlessServiceInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface BoundlessServiceInstanceRepository extends
		CrudRepository<BoundlessServiceInstance, String> {

}
