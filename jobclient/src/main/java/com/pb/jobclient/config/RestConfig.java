package com.pb.jobclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        // TODO: add proxy configuration
    	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    	//factory.setBufferRequestBody(false);
    	factory.setReadTimeout(3000000);
    	factory.setConnectTimeout(300000);
    	
        RestTemplate rest = new RestTemplate(factory);

        HttpMessageConverter<?> formHttpMessageConverter = new FormHttpMessageConverter();
        List<HttpMessageConverter<?>> messageConverters = rest.getMessageConverters();
        messageConverters.add(formHttpMessageConverter);

        rest.setMessageConverters(messageConverters);

        return rest;
    }
}
