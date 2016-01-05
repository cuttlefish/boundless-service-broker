package org.boundless.cf.servicebroker.cfutils;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@SuppressWarnings("rawtypes")
public class VoidSubcriber<T > implements Subscriber<T> {
	
	private static final Logger log = Logger.getLogger(VoidSubcriber.class);
	
	protected Subscription subscription;
	protected CFEntityType resource;
	protected CFResourceEntity cfEntity;
	
	public VoidSubcriber(CFEntityType resourceType) {
		this.resource = resourceType;
	}
	
	public CFEntityType getResource() {
		return resource;
	}

	public void setResource(CFEntityType resource) {
		this.resource = resource;
	}

	public void onSubscribe(Subscription s) { 
		log.debug("Subscription started: " + s); this.subscription = s; 
		try {
	        s.request(1); // Our Subscriber is unbuffered and modest, it requests one element at a time
	      } catch(final Throwable t) {
	        (new IllegalStateException(s + " violated the Reactive Streams rule 3.16 by throwing an exception from request.", t)).printStackTrace(System.err);
	      }
	}
	
	@SuppressWarnings("unchecked")
	public void onNext(T t) { 
		log.debug("Subscription onNext current item: " + t);
	
	try {
	    @SuppressWarnings("rawtypes")
	    
		//PaginatedResponse<Resource> response = (PaginatedResponse<Resource>) t;
	    PaginatedResponse response = (PaginatedResponse)t;
	    List<Resource> resourceList = response.getResources();
	    
	    log.debug("resourceList:  " + resourceList);
		if (resourceList.size() == 1) {
			cfEntity = new CFResourceEntity();
			cfEntity.setType(resource);
			
			Resource firstEntry = resourceList.get(0);
			cfEntity.setId(resourceList.get(0).getMetadata().getId());
	        
			Object resourceEntity = resourceList.get(0).getEntity();				
			Method entityMethod = firstEntry.getClass().getMethod("getEntity");
	        Object entityObject = entityMethod.invoke(firstEntry, new Object[]{});
	        
			// The nested entity is of type Object in the base Resource
			// Use reflection to invoke getName()
			Method m = entityObject.getClass().getMethod("getName");
	        cfEntity.setName( (String) m.invoke(entityObject, new Object[] {}));
			
			log.info("Created CFResourceEntity: " + cfEntity);
		}
		} catch(Exception e) { e.printStackTrace();}
		
		subscription.request(1);
	}
		
    public CFResourceEntity getEntity() {
		return cfEntity;
	}

	public void setEntity(CFResourceEntity entity) {
		this.cfEntity = entity;
	}

	public void onError(Throwable t) { log.info("Subscription error!! " + t); t.printStackTrace(); }
   
    public void onComplete() { log.debug("Subscription done:"); }
}
