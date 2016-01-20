package org.boundless.cf.servicebroker.repository;

import org.boundless.cf.servicebroker.model.BoundlessServiceInstanceMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface BoundlessAppMetadataRepository extends
		CrudRepository<BoundlessServiceInstanceMetadata, String> {

}
