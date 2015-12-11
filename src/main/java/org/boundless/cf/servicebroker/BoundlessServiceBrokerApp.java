package org.boundless.cf.servicebroker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boundless.cf.servicebroker.cfutils.CFAppManager;
import org.boundless.cf.servicebroker.service.BeanCatalogService;
import org.boundless.cf.servicebroker.service.CatalogService;
import org.boundless.cf.servicebroker.servicebroker.model.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class BoundlessServiceBrokerApp  extends WebMvcConfigurerAdapter{

	Log log = LogFactory.getLog(BoundlessServiceBrokerApp.class);
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private CFAppManager cfAppHelper;
	
	public static void main(String[] args) {
		SpringApplication.run(BoundlessServiceBrokerApp.class, args);
	}
	
	
    @Bean
    FilterRegistrationBean brokerApiVersionFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new BrokerApiVersionFilter());
        bean.addUrlPatterns("/v2/*");
        return bean;
    }
    
    @Bean
    FilterRegistrationBean corsFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new SimpleCORSFilter());
        bean.addUrlPatterns("/*");

        return bean;
    }
    
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    

	@Bean
	@ConditionalOnMissingBean(CatalogService.class)
	public CatalogService beanCatalogService(Catalog catalog) {
		return new BeanCatalogService(catalog);
	}


}
