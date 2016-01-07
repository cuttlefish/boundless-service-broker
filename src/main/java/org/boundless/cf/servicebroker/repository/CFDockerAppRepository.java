package org.boundless.cf.servicebroker.repository;

import org.boundless.cf.servicebroker.model.BoundlessAppMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface CFDockerAppRepository extends
		CrudRepository<BoundlessAppMetadata, String> {

}
