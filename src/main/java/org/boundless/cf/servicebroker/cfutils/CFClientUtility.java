/*
 * Copyright 2013-2015 the original author or authors.
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

package org.boundless.cf.servicebroker.cfutils;

import java.util.Arrays;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import reactor.rx.Streams;

@Configuration
@EnableAutoConfiguration
@Lazy
public class CFClientUtility {

	/*
	@Bean
	SpringCloudFoundryClient cloudFoundryClient(@Value("${cf.target}") String host,
			@Value("${cf.username}") String username,
			@Value("${cf.password}") String password,
			@Value("${cf.skipSslValidation:true}") Boolean skipSslValidation) {

		host = "https://api.classic.coke.cf-app.com";
		username = "admin";
		password = "f303d88a110c930a91ef";
		
		return SpringCloudFoundryClient.builder()
				.host(host)
				.username(username)
				.password(password)
				.skipSslValidation(skipSslValidation)
				.build();
	}
	*/

	@Bean
	SpringCloudFoundryClient cloudFoundryClient(
			@Value("${cf.target}") String cfTarget,
			@Value("${cf.admin.username}") String cfUsername,
			@Value("${cf.admin.password}") String cfPassword,
			@Value("${cf.skipSslValidation:true}") Boolean skipSslValidation) {

		/*
		String host = "api.classic.coke.cf-app.com";
		String username = "admin";
		String password = "f303d88a110c930a91ef";
		*/
		
		return SpringCloudFoundryClient.builder()
				.host(cfTarget)
				.username(cfUsername)
				.password(cfPassword)
				.skipSslValidation(skipSslValidation)
				.build();
	}

	@Bean
	public SpringLoggregatorClient loggregatorClient(SpringCloudFoundryClient cloudFoundryClient) {
		return SpringLoggregatorClient.builder()
				.cloudFoundryClient(cloudFoundryClient)
				.build();
	}



}