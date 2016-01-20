package org.boundless.cf.servicebroker.cfutils;

import org.boundless.cf.servicebroker.model.AppMetadata;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Promise;
import reactor.rx.Stream;

public class ReactorAppWrapper {
	
	private final CloudFoundryClient cloudFoundryClient;
	private final AppMetadata appMetadata;
	private final Mono<String> spaceId;
	private final Mono<String> organizationId;
	private final Mono<String> domainId;
	private Mono<String> routeId;
	private Mono<String> appId;
	
	
	@Autowired
	ReactorAppWrapper(CloudFoundryClient cloudFoundryClient) { //, AppMetadata appMetadata) {
		
		this.appMetadata = createAppMetadata();		
		
		this.cloudFoundryClient = cloudFoundryClient;
		this.organizationId = requestOrganizationId(cloudFoundryClient, appMetadata.getOrg());
		this.spaceId = requestSpaceId(cloudFoundryClient, organizationId, appMetadata.getSpace());
		this.domainId = requestDomainId(cloudFoundryClient, appMetadata.getDomain());
	}
	

	private AppMetadata createAppMetadata() {
		AppMetadata appMetadata = new AppMetadata();
		this.appMetadata.setName("testApp");
		this.appMetadata.setOrg("dev");
		this.appMetadata.setSpace("dev");
		this.appMetadata.setRoute("testAppRoute");
		this.appMetadata.setDomain("pcfaas-slot8.pez.pivotal.io");
		this.appMetadata.setDockerImage("bonzofenix/spring-music");
		this.appMetadata.setMemory(512);
		this.appMetadata.setDisk(512);
		return appMetadata;
	}
	
	public Mono<Void> apply(Throwable throwable) {
        if (throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() != null) {
            return Mono.empty();
        } else {
            return Mono.error(throwable);
        }
    }
	
	
	Mono<Object> push() {
		this.routeId = this.domainId
				.then(domainId -> requestCreateRoute(domainId, appMetadata.getRoute()));
		
		this.appId = this.spaceId
			.then(spaceId -> createApplication(spaceId, appMetadata.getName()));
		
		
		Mono<Void> updateApplication = this.appId
				.then(this::updateApplication);
		
		return Mono
				.when(this.appId, this.routeId)
				.then(this::requestAssociateRoute)
				.then(this::updateApplication)
				.then(s -> Mono.empty())
				.otherwise(new Function<Throwable, Mono<Void>>() {

	                    @Override
	                    public Mono<Void> apply(Throwable throwable) {
	                        if (throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() != null) {
	                            return Mono.empty();
	                        } else {
	                            return Mono.error(throwable);
	                        }
	                    }
                }); 
		
	}
	
	Mono<Void> delete() {
		
		return Mono
				.when(this.appId, this.routeId)
				.then(this::requestRemoveRoute)		
			    .then(this::deleteApplication);
		
		/*
				.then(this::deleteApplication)
				.then(s -> Mono.empty())
				.otherwise(new Function<Throwable, Mono<Void>>() {

	                    @Override
	                    public Mono<Void> apply(Throwable throwable) {
	                        if (throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() != null) {
	                            return Mono.empty();
	                        } else {
	                            return Mono.error(throwable);
	                        }
	                    }
                }); 
		
		return Mono
				.when(this.appId, this.routeId)
				.then(this::requestAssociateRoute)
				.then(this::deleteApplication)
				.then(s -> Mono.empty())
				.otherwise(new Function<Throwable, Mono<Void>>() {

	                    @Override
	                    public Mono<Void> apply(Throwable throwable) {
	                        if (throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() != null) {
	                            return Mono.empty();
	                        } else {
	                            return Mono.error(throwable);
	                        }
	                    }
                }); 
		*/
	}

	
	private Mono<String> requestDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
		ListDomainsRequest request = ListDomainsRequest.builder()
				.name(domain)
				.build();
		
		return cloudFoundryClient.domains().list(request)
				.flatMap(response -> Stream.fromIterable(response.getResources()))
				.as(Stream::from)
				.single()
				.map(resource -> resource.getMetadata().getId());
				//.to(Promise.prepare());
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
				//.to(Promise.prepare());
	}
	
	private static Mono<String> requestSpaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, String space) {
		return organizationId
			.then(organizationId2 -> {
				ListSpacesRequest request = ListSpacesRequest.builder()
						.name(space)
						.organizationId(organizationId2)
						.build();
				
				return cloudFoundryClient.spaces().list(request)
						.flatMap(response -> Stream.fromIterable(response.getResources()))
						.as(Stream::from)
						.single()
						.map(resource -> resource.getMetadata().getId());
			});
	}
	
	private final Mono<String> createApplication(String spaceId, String applicationName) {
		CreateApplicationRequest request = CreateApplicationRequest.builder()
				.name(applicationName)
				.spaceId(spaceId)
				.build();
				
		return this.cloudFoundryClient.applicationsV2().create(request)
									.map(response -> response.getMetadata().getId());
	}
	
	private final Mono<Void> updateApplication(String appId) {
		UpdateApplicationRequest request = UpdateApplicationRequest.builder()
				.id(appId)
				.state("STARTED")
				.build();
				
		return this.cloudFoundryClient.applicationsV2().update(request)
									.map(response -> response.getMetadata())
									.then(s -> Mono.empty());		
	}	

	private final Mono<Void> deleteApplication(String appId) {
		//String appId = params.t1;
		DeleteApplicationRequest request = DeleteApplicationRequest.builder()
	            .id(appId)
	            .build();
		
		return this.cloudFoundryClient.applicationsV2().delete(request)
				.then(s -> Mono.empty());		
	}
	
	private final Mono<String> requestCreateRoute(String domainId, String host) {
		CreateRouteRequest request = CreateRouteRequest.builder()
				.host(host)
				.domainId(domainId)
				.build();
				
		return this.cloudFoundryClient.routes().create(request)
									.map(response -> response.getMetadata().getId());
	}
	
	private final Mono<String> requestAssociateRoute(Tuple2<String, String> tuple) {
		String applicationId = tuple.t1;
		String routeId = tuple.t2;
		
		/*
		Mono
			.when (appId, routeId)
			.then(tuple -> {
					String applicationId = tuple.t1;
					String routeId2 = tuple.t2;
		*/		
			
		
		AssociateApplicationRouteRequest request = AssociateApplicationRouteRequest.builder()
				.id(applicationId)
                .routeId(routeId)
                .build();
                
        return this.cloudFoundryClient.applicationsV2().associateRoute(request)
									.map(response -> response.getMetadata().getId());
	}
	
	/*
	private final Mono<Void> requestRemoveRoute(Tuple2<String, String> tuple) {
		String applicationId = tuple.t1;
		String routeId = tuple.t2;
	*/
	
	private final Mono<String> requestRemoveRoute(Tuple2<String, String> params) {
		/*
		Mono
			.when (appId, routeId)
			.then(tuple -> {
					String applicationId = tuple.t1;
					String routeId2 = tuple.t2;
		*/		
			
		String appId = params.t1;
		String routeId = params.t2;
		RemoveApplicationRouteRequest request = RemoveApplicationRouteRequest.builder()
				.id(appId)
                .routeId(routeId)
                .build();
                
        return this.cloudFoundryClient.applicationsV2().removeRoute(request)
									.then(s -> Mono.just(appId));
	}


}
