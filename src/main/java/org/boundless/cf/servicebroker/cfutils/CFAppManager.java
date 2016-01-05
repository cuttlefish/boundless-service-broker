package org.boundless.cf.servicebroker.cfutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.servicebroker.model.AppMetadata;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import reactor.rx.Stream;
import reactor.rx.Streams;

@Configuration
@EnableAutoConfiguration
public class CFAppManager {
	
	@Autowired 
	SpringCloudFoundryClient cfClient;

	private static final Logger log = Logger.getLogger(CFAppManager.class);
    
    private <T extends PaginatedRequest, U extends PaginatedResponse> Stream<U> paginate(
            Function<Integer, T> requestProvider, Function<T, Publisher<U>> operationExecutor) {

        return Streams.just(Streams.wrap(operationExecutor.apply(requestProvider.apply(1))))
                .concatMap(responseStream -> responseStream
                        .take(1)
                        .concatMap(response -> Streams.range(2, response.getTotalPages() - 1)
                                        .flatMap(page -> operationExecutor.apply(requestProvider.apply(page)))
                                        .startWith(response)
                        ));
    }
    
    public SpringCloudFoundryClient getCfClient() {
    	return cfClient;
    }

	//@Bean
	public String orgId(String organization) {
		if (organization == null)
			return null;
		
		ListOrganizationsRequest request = ListOrganizationsRequest.builder()
				.name(organization)
				.build();

		return Streams.wrap(cfClient.organizations().list(request))
				.flatMap(response -> Streams.from(response.getResources()))
				.map(resource -> resource.getMetadata().getId())
				.next().poll();
	}

	//@Bean
	public String spaceId(String organizationId, /*@Value("${test.space}")*/ String space) {
		if (organizationId == null || space == null) 
			return null;
		
		ListSpacesRequest request = ListSpacesRequest.builder()
				.organizationId(organizationId)
				.name(space)
				.build();

		return Streams.wrap(cfClient.spaces().list(request))
				.flatMap(response -> Streams.from(response.getResources()))
				.map(resource -> resource.getMetadata().getId())
				.next().poll();
	}

	//@Bean
	public String appId(String organizationId, String spaceId, String app) {

		if (organizationId == null || spaceId == null || app == null)
			return null;
		
		ListApplicationsRequest request = ListApplicationsRequest.builder()
        		.names(Arrays.asList( app))
        		.organizationIds(Arrays.asList( organizationId) )
        		.spaceIds(Arrays.asList( spaceId))
                .build();
        
		return Streams.wrap(cfClient.applicationsV2().list(request))
				.flatMap(response -> Streams.from(response.getResources()))
				.map(resource -> resource.getMetadata().getId())
				.next().poll();
	}
	

	

    
    private Publisher<ListOrganizationsResponse> listOrgs(String org) {
    	ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                .name(org)
                .build();

        return this.cfClient.organizations().list(request);
    }
    
    private Publisher<ListSpacesResponse> listSpaces(String orgId, String space) {
    	List<String> organizationIds  = new ArrayList<String>();
    	organizationIds.add(orgId);
    	
        ListSpacesRequest request = ListSpacesRequest.builder()
                .name(space)
                .organizationIds(organizationIds)
                .build();

        return this.cfClient.spaces().list(request);
    }
    
    private Publisher<ListApplicationsResponse> listApplications(String orgId, String spaceId, String appName) {
    	List<String> organizationIds  = new ArrayList<String>();
    	List<String> spaceIds = new ArrayList<String>();
    	List<String> appNames = new ArrayList<String>();
    	
    	organizationIds.add(orgId);
    	spaceIds.add(spaceId);
    	appNames.add(appName);    	
    	
        ListApplicationsRequest request = ListApplicationsRequest.builder()
        		.names(appNames)
        		.organizationIds(organizationIds)
        		.spaceIds(spaceIds)
                .build();
        log.info("ApplicationRequest: " + request);
        return this.cfClient.applicationsV2().list(request);
    }

    private String  mapOrg(String orgName) {
    	Publisher<ListOrganizationsResponse> listOrgsResponse = listOrgs(orgName);
    	System.out.println("ListOrgResponse: " + listOrgsResponse.toString());
    	
    	SimpleSubcriber<PaginatedResponse> subscriber = new SimpleSubcriber<PaginatedResponse>(CFEntityType.ORGANIZATION);
    	listOrgsResponse.subscribe(subscriber);
    	
    	CFResourceEntity orgEntity = subscriber.getEntity();
    	if (orgEntity == null) {
    		throw new IllegalStateException("Could not find org " + orgName);
    	}
    	
    	log.info("Got org entity: " + orgEntity);
    	return orgEntity.getId();
    }
    
    private String mapSpace(String orgId, String spaceName) {
    	
    	Publisher<ListSpacesResponse> listSpacesResponse = listSpaces(orgId, spaceName);
    
		System.out.println("ListSpaceResponse: " + listSpacesResponse.toString());
		
		SimpleSubcriber<PaginatedResponse> subscriber = new SimpleSubcriber<PaginatedResponse>(CFEntityType.SPACE);
		listSpacesResponse.subscribe(subscriber);
		
		CFResourceEntity spaceEntity = subscriber.getEntity();
		if (spaceEntity == null) {
			throw new IllegalStateException("Could not find space: " + spaceName+ " within org:" + orgId);
		}
		log.info("Got space entity: " + spaceEntity);
		return spaceEntity.getId();				
    }
    
    private String mapApp(String orgId, String spaceId, String appName) {
    	
    	Publisher<ListApplicationsResponse> listAppsResponse = listApplications(orgId, spaceId, appName);
    	System.out.println("ListAppResponse: " + listAppsResponse.toString());
    	
    	SimpleSubcriber<PaginatedResponse> appSubscriber 
    				= new SimpleSubcriber<PaginatedResponse>(CFEntityType.APPLICATION);
    	listAppsResponse.subscribe(appSubscriber);
    	
    	AppResponseSubcriber appResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
    	
    	CFResourceEntity appEntity = appSubscriber.getEntity();
    	
    	if (appEntity == null)
    		return null;
    	
    	log.info("Got app entity: " + appEntity);
		return appEntity.getId();				
    }
    
    private String  mapDpmain(String domainName) {
    	ListDomainsRequest request = null;
    	ListDomainsRequest.ListDomainsRequestBuilder builder = ListDomainsRequest.builder();
    	/*
    	if (domainName != null) {
    		request = builder.name(domainName).build();
    		return Streams.wrap(cfClient.domains().list(request))
    				.flatMap(response -> Streams.from(response.getResources()))
    				.map(resource -> resource.getMetadata().getId())
    				.next().poll();
    	}
    	*/ 
		
    	request = builder.build();
		return Streams.wrap(cfClient.domains().list(request))
				.flatMap(response -> Streams.from(response.getResources()))
				.map(resource -> resource.getMetadata().getId())
				.next().poll();
    	
    }
    
    public String createDomain(String organizationId, String domainName) {
    	
    	try {
    		return mapDpmain(domainName);    		
    	} catch(Exception e) { 
    		log.error("Domain " + domainName + " does not exist, creating newly"); 
    	}
    	
    	CreateDomainRequest organization = CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build();

    	try {
	        String domainId = (String) Streams.wrap(this.cfClient.domains().create(organization))
	        		.map(resource -> resource.getMetadata().getId())
	                .next().poll();
	        
	        return domainId;
    	} catch(Exception e) { e.printStackTrace(); }
    	
    	// FIX ME
    	return null;
    }
    
    private void createAppRequest(AppMetadata appRequest) {
		CreateApplicationRequest appCreationRequest = CreateApplicationRequest.builder()
                .spaceId(appRequest.getSpaceGuid())
                .name(appRequest.getApp())
                .diego(true)
                .dockerImage(appRequest.getDockerImage())
                .instances(appRequest.getInstances())
                .memory(appRequest.getMemory())
                .diskQuota(appRequest.getDisk())
                .healthCheckTimeout(180)
                .build();

    	log.info("Created CreateAppRequest: " + appCreationRequest);			
    	
		Publisher<CreateApplicationResponse> appCreationResponse = this.cfClient.applicationsV2().create(appCreationRequest);
		AppResponseSubcriber appResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
		appCreationResponse.subscribe(appResponseSubscriber);	
		log.info("CreateAppResponse: " + appCreationResponse);
		
		String appGuid = appResponseSubscriber.getEntity().getId();	
		appRequest.setAppGuid(appGuid);
		log.info("Created App with Guid: " + appGuid);
    }
    
    private void updateAppRequest(AppMetadata appRequest) {
    	UpdateApplicationRequest appUpdateRequest = UpdateApplicationRequest.builder()
                .spaceId(appRequest.getSpaceGuid())
                .name(appRequest.getApp())
                .diego(true)
                .state(appRequest.getState())
                .dockerImage(appRequest.getDockerImage())
                .instances(appRequest.getInstances())
                .memory(appRequest.getMemory())
                .diskQuota(appRequest.getDisk())
                .healthCheckTimeout(180)
                .id(appRequest.getAppGuid())
                .build();

    	log.info("Created UpdateAppRequest: " + appUpdateRequest);
		
    	Publisher<UpdateApplicationResponse> appStartUpdationResponse = this.cfClient.applicationsV2().update(appUpdateRequest);
    	AppResponseSubcriber appStartResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
    	appStartUpdationResponse.subscribe(appStartResponseSubscriber);
    	log.info("UpdateAppResponse: " + appStartUpdationResponse);
    }
    
    private void associateRouteWithAppRequest(AppMetadata appRequest) {
    	AssociateApplicationRouteRequest routeRequest = AssociateApplicationRouteRequest.builder()
                .id(appRequest.getAppGuid())
                .routeId(appRequest.getRouteGuid())
                .build();
    	
    	Publisher<AssociateApplicationRouteResponse> appRouteResponse = this.cfClient.applicationsV2().associateRoute(routeRequest);
    	AppResponseSubcriber appRouteResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
    	appRouteResponse.subscribe(appRouteResponseSubscriber);
    	log.info("Route associated: " + appRouteResponse);    	
    }
    
    private void deleteAppRequest(AppMetadata appRequest) {
		DeleteApplicationRequest appDeletionRequest = DeleteApplicationRequest.builder()
	            .id(appRequest.getAppGuid())
	            .build();
    	log.info("Created app deletion request: " + appDeletionRequest);			
    	
    	Publisher<Void> deleteAppResponse = this.cfClient.applicationsV2().delete(appDeletionRequest);
    	VoidSubcriber deleteAppResponseSubscriber = new VoidSubcriber(CFEntityType.SPACE);
    	deleteAppResponse.subscribe(deleteAppResponseSubscriber);
    	log.info("Delete App response: " + deleteAppResponse);	
    }

    private void createRouteRequest(AppMetadata appRequest) {
    	CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                .domainId(appRequest.getDomainGuid())
                .spaceId(appRequest.getSpaceGuid())
                .host(appRequest.getRouteName())
                .build();
    	
    	String routeId = Streams
                .wrap(this.cfClient.routes().create(createRouteRequest))
                .map(resource -> resource.getMetadata().getId())
                .next()
                .poll();
    	log.info("Got Route associated: " + routeId	);	
    	appRequest.setRouteGuid(routeId);
    }
    
    private void deleteRouteRequest(AppMetadata appRequest) {
    	DeleteRouteRequest deleteRouteRequest = DeleteRouteRequest.builder()
                .id(appRequest.getRouteGuid())
                .build();
    	
    	Publisher<Void> deleteRouteResponse = this.cfClient.routes().delete(deleteRouteRequest);
    	VoidSubcriber deleteRouteResponseSubscriber = new VoidSubcriber(CFEntityType.SPACE);
    	deleteRouteResponse.subscribe(deleteRouteResponseSubscriber);
    	log.info("Delete Route response: " + deleteRouteResponse);	
    }

    public void pushApp(AppMetadata appRequest)  {
    	
    	//String org, String space, String dockerImage, String appName;
    	/*
    	String org = "dev";
    	String space = "dev";
    	String dockerImage = "bonzofenix/spring-music";
    	String appName = "test-docker";
    	String routePath = "test-docker-route";
    	*/    	
    	
    	System.out.println("cfClient: " + cfClient);
    	String orgId = mapOrg(appRequest.getOrg());    	
    	String spaceId = mapSpace(orgId, appRequest.getSpace());
    	appRequest.setOrgGuid(orgId);
    	appRequest.setSpaceGuid(spaceId);
    	
    	String appGuid = mapApp( orgId, spaceId, appRequest.getApp());
    	if (appGuid == null) {
    		createAppRequest(appRequest);
    	}     	
    	
    	String domainName = appRequest.getDomain();    	
    	String domainId = createDomain(orgId, domainName);
    	log.info("Found Domain Id: " + domainId + " for domain: " + domainName);	
    	appRequest.setDomainGuid(domainId);    	
    	createRouteRequest(appRequest);
    	
    	appRequest.setState("STARTED");
    	updateAppRequest(appRequest);
    	
    	/*
    	Streams.wrap(cfClient.applicationsV2().list(request))
		.flatMap(response -> Streams.from(response.getResources()))
		.map(resource -> resource.getMetadata().getId())
		.next().poll();
    	*/
    	    	
    	associateRouteWithAppRequest(appRequest);
    	return; 
    }
    
    public void deleteApp(AppMetadata appRequest) {
    	if (appRequest.getAppGuid() == null) {
    		log.info("Nothing to delete, returning!!");	
    		return;
    	}
    	
    	deleteRouteRequest(appRequest);
		deleteAppRequest(appRequest);	
    		
    	log.info("Finished app deletion request for App Guid: " + appRequest.getAppGuid());	
    	return; 
    }
    
    
    
    
}
