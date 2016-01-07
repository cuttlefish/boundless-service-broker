package org.boundless.cf.servicebroker.cfutils;

import org.apache.log4j.Logger;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;

public class AppResponseSubcriber extends SimpleSubcriber<Resource<ApplicationEntity>> {
	
	private static final Logger log = Logger.getLogger(AppResponseSubcriber.class);	

	public AppResponseSubcriber(CFEntityType resourceType) {
		super(resourceType);
		// TODO Auto-generated constructor stub
	}
	
	public void onNext(Resource<ApplicationEntity> t) { 		
		log.debug("AppResponse-Subscription onNext : " + t);
	
		Resource<ApplicationEntity> response = (Resource<ApplicationEntity>)t;
		log.debug("Response metadata: " + response.getMetadata());
		log.debug("Response entity: " + response.getEntity());
		
		cfEntity = new CFResourceEntity();			
		
		cfEntity.setType(resource);			
		cfEntity.setId(response.getMetadata().getId());
		
		ApplicationEntity resourceEntity = response.getEntity();
		cfEntity.setName(resourceEntity.getName());
		
		log.debug("Created CFResourceEntity: " + cfEntity);
		subscription.request(1);
	}	
}
