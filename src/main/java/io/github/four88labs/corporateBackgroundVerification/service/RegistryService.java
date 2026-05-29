package io.github.four88labs.corporateBackgroundVerification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Async
public class RegistryService {
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(WatchlistService.class);

    public RegistryService(RestTemplate restTemplate, ObjectMapper objectMapper){
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Future<String> getOrgValidity(String registrationNumber, String countryCode) throws Exception {
            URI uri = UriComponentsBuilder.fromUriString("http://localhost:8081/api/v1/registry/validate")
                    .queryParam("t", registrationNumber)
                    .queryParam("country", countryCode)
                    .build().toUri();
            logger.info("Get Org Status for registrationNumber {} and countryCode {}", registrationNumber, countryCode);
            logger.info("Get Org Status URI {}", uri.toString());
            String response = restTemplate.getForObject(uri,String.class);
            //String response = null;
            return CompletableFuture.supplyAsync(() ->response);
    }
}
