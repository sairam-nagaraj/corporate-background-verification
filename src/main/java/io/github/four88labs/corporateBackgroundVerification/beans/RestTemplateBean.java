package io.github.four88labs.corporateBackgroundVerification.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateBean {
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();

        factory.setConnectionRequestTimeout(1000);      // Time to establish connection
        factory.setReadTimeout(1000);         // Time to wait for response data
        factory.setConnectionRequestTimeout(1000);
        return new RestTemplate(factory);
    }
}
