package org.boundless.cf.servicebroker.service.boundless;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.repository.ServiceDefinitionnRepository;
import org.boundless.cf.servicebroker.service.CatalogService;
import org.boundless.cf.servicebroker.servicebroker.model.Catalog;
import org.boundless.cf.servicebroker.servicebroker.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoundlessCatalogService implements CatalogService {

	private static final Logger LOG = Logger.getLogger(BoundlessCatalogService.class);

	@Autowired
	ServiceDefinitionnRepository serviceRepo;

	@Override
	public Catalog getCatalog() {		
		
		return new Catalog(IteratorUtils.toList(serviceRepo.findAll().iterator()));
	}

	@Override
	public ServiceDefinition getServiceDefinition(String id) {
		if (id == null) {
			return null;
		}

		for (ServiceDefinition sd : getCatalog().getServiceDefinitions()) {
			if (sd.getId().equals(id)) {
				return sd;
			}
		}
		return null;
	}

}