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
public class WatchlistService {

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(WatchlistService.class);

    public WatchlistService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public Future<String> getOrgStatus(String countryCode, String companyName) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8081/api/v1/watchlist-check")
                .queryParam("t", companyName)
                .queryParam("country", countryCode)
                .build().toUri();
        logger.info("Get Org Status for companyName {} and countryCode {}", companyName, countryCode);
        logger.info("Get Org Status URI {}", uri.toString());
        String response = restTemplate.getForObject(uri,String.class);
        return CompletableFuture.supplyAsync(() -> response);

    }
}
