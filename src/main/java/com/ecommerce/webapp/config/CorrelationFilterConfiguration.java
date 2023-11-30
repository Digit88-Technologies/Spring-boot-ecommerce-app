package com.ecommerce.webapp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class CorrelationFilterConfiguration {
	
	public static final String DEFAULT_HEADER_TOKEN = "correlationId";
	public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "correlationId";

	private String responseHeader = DEFAULT_HEADER_TOKEN;
	private String mdcKey = DEFAULT_MDC_UUID_TOKEN_KEY;
	private String requestHeader = DEFAULT_HEADER_TOKEN;

	@Bean
	public FilterRegistrationBean<CorrelationFilter> correlationFilterBeanClass() {
		FilterRegistrationBean<CorrelationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CorrelationFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

}
