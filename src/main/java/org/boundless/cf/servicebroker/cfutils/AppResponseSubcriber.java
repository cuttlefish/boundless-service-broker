package org.boundless.cf.servicebroker.cfutils;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class AppResponseSubcriber extends SimpleSubcriber<Resource<ApplicationEntity>> {
	
	public AppResponseSubcriber(CFEntityType resourceType) {
		super(resourceType);
		// TODO Auto-generated constructor stub
	}


	private Log log = LogFactory.getLog(AppResponseSubcriber.class);	
	
	public void onNext(Resource<ApplicationEntity> t) { 		
		log.info("AppResponse-Subscription onNext : " + t);
	
		Resource<ApplicationEntity> response = (Resource<ApplicationEntity>)t;
		log.info("Response metadata: " + response.getMetadata());
		log.info("Response entity: " + response.getEntity());
		
		cfEntity = new CFResourceEntity();			
		
		cfEntity.setType(resource);			
		cfEntity.setId(response.getMetadata().getId());
		
		ApplicationEntity resourceEntity = response.getEntity();
		cfEntity.setName(resourceEntity.getName());
		
		log.info("Created CFResourceEntity: " + cfEntity);
		subscription.request(1);
	}
	
		
}
