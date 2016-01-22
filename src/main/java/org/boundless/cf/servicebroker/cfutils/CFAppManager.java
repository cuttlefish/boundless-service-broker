package org.boundless.cf.servicebroker.cfutils;

import java.util.HashMap;

/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author: bhale@pivotal.io
 * @author: sabhap@pivotal.io
 */

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.boundless.cf.servicebroker.model.AppMetadataDTO;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest.CreateApplicationRequestBuilder;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest.UpdateApplicationRequestBuilder;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

public class CfAppManager {

	private AppMetadataDTO appMetadata;
	private Mono<String> spaceId;
	private Mono<String> organizationId;
	private Mono<String> domainId;
	private String appName;
	
    private CloudFoundryClient cloudFoundryClient;
    private static final Logger log = Logger.getLogger(CfAppManager.class);
    
    public CfAppManager(CloudFoundryClient cloudFoundryClient, AppMetadataDTO appMetadata) {
    	this.cloudFoundryClient = cloudFoundryClient;
    	init(appMetadata);
    }
    
    
    public void init(AppMetadataDTO appMetadata) {
    	this.appMetadata = appMetadata;	
    	this.appName = appMetadata.getName();
		log.debug("Looking up orgId, spaceId, domainId with cfclient");
		
		if (appMetadata.getOrgGuid() == null || appMetadata.getSpaceGuid() == null ) {
	        this.organizationId = requestOrganizationId(cloudFoundryClient, appMetadata.getOrg());
	        this.spaceId = requestSpaceId(cloudFoundryClient, this.organizationId, appMetadata.getSpace());
		} else {
			this.organizationId = Mono.just(appMetadata.getOrgGuid());
			this.spaceId =  Mono.just(appMetadata.getSpaceGuid());
		}

        this.domainId = requestDomainId(cloudFoundryClient, appMetadata.getDomain());
    }

	public Map<String, String> getTopLevelGuids() {
		Map<String, String> map = new HashMap<String, String>();
		
		if (organizationId != null)
			map.put("OrgGuid", organizationId.get());
		
		if (spaceId != null)
			map.put("SpaceGuid", spaceId.get());
		
		if (domainId != null)
			map.put("DomainGuid", domainId.get());
		
		return map;
	}

    public Mono<Tuple2<String, String>> push() throws Exception {
    	log.info("App push for: " + this.appMetadata);
    	
    	Mono<String> routeId = Mono
                .when(this.domainId, this.spaceId)
                .then(tuple -> {
                    String domainId2 = tuple.t1;
                    String spaceId2 = tuple.t2;

                    return requestRouteId(this.cloudFoundryClient, domainId2, spaceId2, this.appMetadata.getRoute());
                });
    	

        Mono<String> appId = this.spaceId
                .then(spaceId2 -> requestCreateApplicationId(this.cloudFoundryClient, 
                											spaceId2, 
                											this.appMetadata.getName(),
                											this.appMetadata.getDockerImage(),
                											this.appMetadata.getInstances(),
                											this.appMetadata.getMemory(),
                											this.appMetadata.getDisk(),
                											this.appMetadata.getStartCommand(),
                											this.appMetadata.getEnvironmentJsons(),
                											this.appMetadata.getDockerCred()
                											))
					.log("stream.invokedRequestCreateApp")
                    .then(applicationId2 -> requesteAppSummary(this.cloudFoundryClient, applicationId2))
                    .log("stream.invokedRequestAppSummary")
                    .then(applicationId3 -> requestUpdateAppState(this.cloudFoundryClient, applicationId3, "STARTED"))
                    .log("stream.invokedUpdateAppState");

        return Mono
                .when(routeId, appId)
                .then(tuple -> {
                    String routeId2 = tuple.t1;
                    String applicationId2 = tuple.t2;
                    return 
	                    Mono.delay(15, TimeUnit.SECONDS)
	                    .then( l -> requestAssociateRoute(this.cloudFoundryClient, applicationId2, routeId2))
	                    .map(r -> tuple);
                })
                .otherwise(throwable ->  {
                	cleanUp(this.cloudFoundryClient, this.spaceId, this.appName, routeId);
                	return Mono.<Tuple2< String, String>>error(new Throwable("App Route association failed, " + throwable.getMessage()));
                });
    }

    public Mono<Void> update() {
    	log.info("App Update for: " + this.appMetadata);
    	if (this.appMetadata.getAppGuid() == null) {
    		return Mono.empty();
    	} else {
    		return requestUpdateApplication(this.cloudFoundryClient, 
								this.appMetadata.getSpaceGuid(), 
								this.appMetadata.getAppGuid(),
								this.appMetadata.getState(),
								this.appMetadata.getDockerImage(),
								this.appMetadata.getInstances(),
								this.appMetadata.getMemory(),
								this.appMetadata.getDisk(),
								this.appMetadata.getStartCommand(),
								this.appMetadata.getEnvironmentJsons(),
								this.appMetadata.getDockerCred()
							);
    	}
    }
    
    public Mono<Void>  delete() {
    	log.info("App Delete for: " + this.appMetadata);
    	if (this.appMetadata.getAppGuid() == null) 
    		return Mono.empty();
    		
    	String routeId = this.appMetadata.getRouteGuid();
    	Mono<String> route = (routeId != null) ? Mono.just(routeId) : Mono.just("");
		return cleanUp(this.cloudFoundryClient, this.spaceId, this.appName, route);
    }

    /*
    private static Mono<Void> cleanUp(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId, String application, Mono<String> routeId) {
    	
    	if (routeId != null) {
    		routeId
    		.then(routeId2 ->  deleteRoute(cloudFoundryClient, routeId2))
    		.log("stream.deleteRoute")
    		.otherwise(throwable -> {
    			log.error("Error with route deletion..." + throwable);
    			throwable.printStackTrace();
    			return Mono.empty();
    		})
    		.after();
    	}
    	
    	return spaceId
                .then(spaceId2 -> deleteApplications(cloudFoundryClient, spaceId2, application));
    }
    */
    
    private static Mono<Void> cleanUp(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId, String application, Mono<String> routeId) {
    	
    	return Mono.when(routeId, spaceId)
    		.then( tuple -> {
    			String routeId2 = tuple.t1;
    			String spaceId2 = tuple.t2;
    			
    			return
    				deleteRoute(cloudFoundryClient, routeId2)
    				.log("stream.preDeleteApp")
    				.and(deleteApplications(cloudFoundryClient, spaceId2, application))
	        		.log("stream.postDeleteApps")
	    			.after();
    		})
    		.otherwise(throwable -> {
    			log.error("Error with app or route deletion..." + throwable);
    			throwable.printStackTrace();
    			return Mono.empty();
    		});
    }

    public static Mono<Void> deleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
    	if (applicationId == null || applicationId == "")
    		return Mono.empty();
    	
        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .id(applicationId)
                .build();

        return Mono.delay(5, TimeUnit.SECONDS)
                .then( l -> cloudFoundryClient.applicationsV2()
                			.delete(request)
                			.log("stream.postDeleteApp"))
                .after();
    }

    public static Mono<Void> deleteApplications(CloudFoundryClient cloudFoundryClient, String spaceId, String application) {
        return Stream
                .from(listApplicationIds(cloudFoundryClient, spaceId, application))
                .log("stream.postListAppIds")
                .flatMap(applicationId -> deleteApplication(cloudFoundryClient, applicationId))
                .log("stream.postDeleteApps")
                .after();
    }

    private static Publisher<String> listApplicationIds(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationId) {
        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .name(applicationId)
                .spaceId(spaceId)
                .build();

        return cloudFoundryClient.applicationsV2().list(request)
                .flatMap(response -> Stream.fromIterable(response.getResources()))
                .map(resource -> resource.getMetadata().getId());
    }

    private static Mono<String> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        AssociateRouteApplicationRequest request = AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .id(routeId)
                .build();
      
        return cloudFoundryClient.routes().associateApplication(request)
        		.log("stream.associateRoute")
        		.map(response -> response.getMetadata().getId());
    }
    
    private static boolean isDockerCredValid(Map<String, String> dockerCredsJson) {
    	return (dockerCredsJson != null
    			&& dockerCredsJson.containsKey("user")
    			&& dockerCredsJson.containsKey("password")
    			&& dockerCredsJson.containsKey("email"));
    }

    private static Mono<String> requestCreateApplicationId(CloudFoundryClient cloudFoundryClient, 
											        		String spaceId, 
											        		String application, 
											        		String dockerImage,  
											        		int instances, 
											        		int memoryQuota, 
											        		int diskQuota, 
											        		String startCommand, 
											        		Map<String, Object> envJson,
															Map<String, String> dockerCredsJson) {
    	CreateApplicationRequestBuilder builder = CreateApplicationRequest.builder()
                .dockerImage(dockerImage)
                .name(application)
                .spaceId(spaceId)
                .diego(true)
                .instances(instances)
                .memory(memoryQuota)
                .diskQuota(diskQuota)
                .healthCheckTimeout(180);
    	
    	if (isDockerCredValid(dockerCredsJson)) {
			builder.dockerCredentialsJsons(dockerCredsJson);
    	} else {
    		log.error("Docker credential map does not contain user, password or email fields, ignoring it");
    	}
    		
    	CreateApplicationRequest request = builder.build();
        return cloudFoundryClient.applicationsV2().create(request)
                .map(response -> response.getMetadata().getId())
                .log("stream.requestCreateApp");
        
    }
    
    private static Mono<String> requestUpdateAppState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
						.id(applicationId)
						.state(state)
						.build();
						
		return cloudFoundryClient.applicationsV2().update(request)
		.map(response -> response.getMetadata().getId())
		.log("stream.requestUpdateAppState");
    }
    
    private static Mono<Void> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, 
											        		String spaceId, 
											        		String applicationId,
											        		String state,
											        		String dockerImage,
											        		int instances, 
											        		int memoryQuota, 
											        		int diskQuota, 
											        		String startCommand, 
											        		Map<String, Object> envJson,
											        		Map<String, String> dockerCredsJson) {

    	UpdateApplicationRequestBuilder builder = UpdateApplicationRequest.builder()
                .dockerImage(dockerImage)
                .instances(instances)
                .memory(memoryQuota)
                .diskQuota(diskQuota)
                .spaceId(spaceId)
                .id(applicationId)
                .state(state)
                .command(startCommand)
                .environmentJsons(envJson);
    	
    	if (isDockerCredValid(dockerCredsJson)) {
			builder.dockerCredentialsJsons(dockerCredsJson);
	    } else {
			log.error("Docker credential map does not contain user, password or email fields, ignoring it");
		}

    	UpdateApplicationRequest request = builder.build(); 
        return cloudFoundryClient.applicationsV2().update(request)
                .map(response -> response.getMetadata().getId())
                .log("stream.requestCompleteUpdateApp")
                .after();
    }

    private static Mono<String> requestCreateRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String host) {
        CreateRouteRequest request = CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .spaceId(spaceId)
                .build();

        return cloudFoundryClient.routes().create(request)
                .map(response -> response.getMetadata().getId())
                .log("stream.requestCreateRoute");
    }
    
    public static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
    	if (routeId == null || routeId == "")
    		return Mono.empty();
    	
    	DeleteRouteRequest request = DeleteRouteRequest.builder()
                .id(routeId)
                .build();
    	
    	return cloudFoundryClient.routes().delete(request)
                .log("stream.DeleteRoute");	
    }

    private static Mono<DomainResource> requestDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        ListDomainsRequest request = ListDomainsRequest.builder()
                .name(domain)
                .build();
        
        return cloudFoundryClient.domains().list(request)
                .flatMap(response -> Stream.fromIterable(response.getResources()))
                .as(Stream::from)
                .singleOrEmpty();
        
    }

    private static Mono<String> requestDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
    	if (domain != null) {
    	return Mono
                .just(domain)
                .then(domain2 -> requestDomain(cloudFoundryClient, domain2))
                .as(Stream::from)                                               
                .switchIfEmpty(requestFirstDomain(cloudFoundryClient))
                .single()                                                       
                .map(resource -> resource.getMetadata().getId());
    	} 
    	
    	return
    		requestFirstDomain(cloudFoundryClient)
   		 	.map(resource -> resource.getMetadata().getId());
    }

    private static Mono<String> requestExistingRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String host) {
        ListRoutesRequest request = ListRoutesRequest.builder()
                .domainId(domainId)
                .host(host)
                .build();

        return cloudFoundryClient.routes().list(request)
                .flatMap(response -> Stream.fromIterable(response.getResources()))
                .as(Stream::from)
                .single()
                .map(resource -> resource.getMetadata().getId());
    }
    
    private static Mono<DomainResource> requestFirstDomain(CloudFoundryClient cloudFoundryClient) {
        ListDomainsRequest request = ListDomainsRequest.builder()
                .build();

        return cloudFoundryClient.domains().list(request)
                .flatMap(response -> Stream.fromIterable(response.getResources()))
                .next();
    }

    private static Mono<String> requestOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                .name(organization)
                .build();

        return cloudFoundryClient.organizations().list(request)
                .flatMap(response -> Stream.fromIterable(response.getResources()))
                .as(Stream::from)
                .single()
                .map(resource -> resource.getMetadata().getId());
    }

    private static Mono<String> requestRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String host) {
        return requestCreateRouteId(cloudFoundryClient, domainId, spaceId, host)
                .as(Stream::from)
                .switchOnError(requestExistingRouteId(cloudFoundryClient, domainId, host))
                .single();
    }
    
    private static Mono<String> requesteAppSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
    	 
		SummaryApplicationRequest request = SummaryApplicationRequest.builder()
		        .id(applicationId)
		        .build();
		
       return cloudFoundryClient.applicationsV2().summary(request)
    		   .log("stream.summaryApp")
    		   .map(response -> response.getId());
    }

    private static Mono<String> requestSpaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, String space) {
        return organizationId
                .then(organizationId2 -> {
                    ListSpacesRequest request = ListSpacesRequest.builder()
                            .organizationId(organizationId2)
                            .name(space)
                            .build();

                    return cloudFoundryClient.spaces().list(request)
                            .flatMap(response -> Stream.fromIterable(response.getResources()))
                            .as(Stream::from)
                            .single()
                            .map(resource -> resource.getMetadata().getId());
                });
    }

}

