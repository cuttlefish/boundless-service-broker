package org.boundless.cf.servicebroker.cfutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boundless.cf.servicebroker.servicebroker.model.AppMetadata;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.rx.Stream;
import reactor.rx.Streams;

@Configuration
@EnableAutoConfiguration
public class CFAppManager {
	
	@Autowired 
	SpringCloudFoundryClient cfClient;

	Log log = LogFactory.getLog(CFAppManager.class);
    
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

    public void pushApp(AppMetadata appRequest) {
    	
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
    	
    	String appGuid = mapApp( orgId, spaceId, appRequest.getApp());
    	appRequest.setAppGuid(appGuid);
    	AppResponseSubcriber appResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
    	
    	if (appGuid == null) {
    		CreateApplicationRequest appCreationRequest = CreateApplicationRequest.builder()
                .spaceId(spaceId)
                .name(appRequest.getApp())
                .diego(true)
                .dockerImage(appRequest.getDockerImage())
                .instances(appRequest.getInstances())
                .memory(appRequest.getMemory())
                .diskQuota(appRequest.getDisk())
                .healthCheckTimeout(180)
                .state(appRequest.getState())
                .build();

	    	log.info("Created CreateAppRequest: " + appCreationRequest);			
	    	
			Publisher<CreateApplicationResponse> appCreationResponse = this.cfClient.applicationsV2().create(appCreationRequest);
			appCreationResponse.subscribe(appResponseSubscriber);			
			appGuid = appResponseSubscriber.getEntity().getId();	
			appRequest.setAppGuid(appGuid);
			
    	} 
    	
    	
		UpdateApplicationRequest appUpdationRequest = UpdateApplicationRequest.builder()
                .spaceId(spaceId)
                .name(appRequest.getApp())
                .diego(true)
                .state("STARTED")
                .dockerImage(appRequest.getDockerImage())
                .instances(appRequest.getInstances())
                .memory(appRequest.getMemory())
                .diskQuota(appRequest.getDisk())
                .healthCheckTimeout(180)
                .state(appRequest.getState())
                .build();

    	log.info("Created UpdateAppRequest: " + appUpdationRequest);
		
    	Publisher<UpdateApplicationResponse> appUpdationResponse = this.cfClient.applicationsV2().update(appUpdationRequest);
		appUpdationResponse.subscribe(appResponseSubscriber);
    	log.info("Updated App to STARTED state: " + appUpdationRequest);
		
    	    	
    	AssociateApplicationRouteRequest routeRequest = AssociateApplicationRouteRequest.builder()
                .id(appGuid)
                .routeId(appRequest.getRouteName())
                .build();
    	
    	//AssociateApplicationRouteResponse appRouteResponse = Streams.wrap(this.cfClient.applicationsV2().associateRoute(routeRequest)).next().get();
    	Publisher<AssociateApplicationRouteResponse> appRouteResponse = this.cfClient.applicationsV2().associateRoute(routeRequest);
    	AppResponseSubcriber appRouteResponseSubscriber = new AppResponseSubcriber(CFEntityType.APPLICATION);
    	appRouteResponse.subscribe(appRouteResponseSubscriber);
    	log.info("Route associated: " + appRouteResponse);
    	
        AssociateApplicationRouteRequest.builder()
        .id(appRequest.getAppGuid())
        .routeId(appRequest.getRouteGuid())
        .build()
        .isValid();
        
    	return; 
    }
    
    public void deleteApp(AppMetadata appRequest) {
    	
    	//String org, String space, String dockerImage, String appName;
    	/*
    	String org = "dev";
    	String space = "dev";
    	String dockerImage = "bonzofenix/spring-music";
    	String appName = "test-docker";
    	String routePath = "test-docker-route";
    	*/
    	
    	if (appRequest.getAppGuid() == null) {
    		log.info("Nothing to delete, returning!!");	
    		return;
    	}
    	
		DeleteApplicationRequest appDeletionRequest = DeleteApplicationRequest.builder()
            .id(appRequest.getAppGuid())
            .build();

    	log.info("Created app deletion request: " + appDeletionRequest);			
    	
		this.cfClient.applicationsV2().delete(appDeletionRequest);
		log.info("Finished app deletion request for App Guid: " + appRequest.getAppGuid());	
    	return; 
    }
    
    
    
    
}
