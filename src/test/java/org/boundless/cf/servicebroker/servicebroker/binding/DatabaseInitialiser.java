package org.boundless.cf.servicebroker.servicebroker.binding;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.boundless.cf.servicebroker.model.BoundlessAppResource;
import org.boundless.cf.servicebroker.model.BoundlessServiceInstance;
import org.boundless.cf.servicebroker.model.BoundlessServiceInstanceMetadata;
import org.boundless.cf.servicebroker.model.Plan;
import org.boundless.cf.servicebroker.model.PlanConfig;
import org.boundless.cf.servicebroker.model.PlanMetadata;
import org.boundless.cf.servicebroker.model.ServiceDefinition;
import org.boundless.cf.servicebroker.model.ServiceInstance;
import org.boundless.cf.servicebroker.model.ServiceInstanceBinding;
import org.boundless.cf.servicebroker.model.ServiceMetadata;
import org.boundless.cf.servicebroker.repository.BoundlessServiceInstanceRepository;
import org.boundless.cf.servicebroker.repository.ServiceDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitialiser  {

	@Autowired
	ServiceDefinitionRepository serviceRepo;

	@Autowired
	BoundlessServiceInstanceRepository serviceInstanceRepo;

    private ServiceDefinition service() {

        ServiceDefinition svc = new ServiceDefinition();
        svc.setName("test-service");
        svc.setId("test-service-id");
        svc.setDescription("test-description");
        svc.setBindable(true);

        ServiceMetadata svcM = new ServiceMetadata();
        svcM.setDisplayName("test-service");
        svcM.setProviderDisplayName("test-provider");
        svcM.setId(0);
        svc.setMetadata(svcM);

        Plan plan = new Plan();
        plan.setName("test-plan");
        plan.setId("test-plan-id");
        plan.setDescription("test-description");
        plan.setFree(true);

        PlanMetadata planM = new PlanMetadata();
        planM.addBullet("basic");
        planM.setId(100);

        plan.setMetadata(planM);

        PlanConfig planDetails = new PlanConfig();
        planDetails.setGeoServerDockerUri("http://test-uri");
        planDetails.setId("100");
        plan.setPlanConfig(planDetails);

        svc.addPlan(plan);
        return svc;
    }

    private BoundlessServiceInstance serviceInstance() {
    	BoundlessServiceInstance svcI = new BoundlessServiceInstance();
    	svcI.setOrgGuid("test-org-id");
    	svcI.setSpaceGuid("test-space-id");
    	svcI.setId("test-service-instance-id");
    	svcI.setPlanId("test-plan-id");
    	svcI.setServiceId("test-service-id");    
    	svcI.setMetadata(serviceInstanceMetdata());
    	return svcI;
    }
    
    private BoundlessServiceInstanceMetadata serviceInstanceMetdata() {
    	BoundlessServiceInstanceMetadata svcIM = new BoundlessServiceInstanceMetadata();
    	svcIM.setOrg("test-org");
    	svcIM.setSpace("test-space");
    	svcIM.setDomain("test-domain");
    	svcIM.setId("test-service-instance-metadata-id");
    	svcIM.setAppResources(new HashSet<BoundlessAppResource>());    	
    	return svcIM;
    }

    private ServiceInstanceBinding serviceBinding() {
    	ServiceInstanceBinding svcB = new ServiceInstanceBinding();
    	svcB.setAppGuid("test-app-guid");
    	svcB.setInstanceId("1");
    	svcB.setId("test-service-binding-instance-id");
    	svcB.setInstanceId("test-service-instance-id");
    	svcB.setPlanId("test-plan-id");
    	svcB.setServiceId("test-service-id");
    	return svcB;
    }

    @PostConstruct
    public void load() {
        // Initialise your database here: create schema, use DBUnit to load data, etc.
		serviceRepo.save(service());
		serviceInstanceRepo.save(serviceInstance());
		System.out.println("Done loading db with service instance...");
		try {
			ServiceInstance si = serviceInstanceRepo.findOne("test-service-instance-id");
			if (si != null)
				System.out.println("Got service instance...");
		} catch(Exception e) { e.printStackTrace(); }
    }


    @PreDestroy
    public void cleanup() {
    	serviceInstanceRepo.delete(serviceInstance());
		serviceRepo.delete("test-service-id");
    }
}
