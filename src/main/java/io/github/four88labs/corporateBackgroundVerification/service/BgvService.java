package io.github.four88labs.corporateBackgroundVerification.service;

import io.github.four88labs.corporateBackgroundVerification.dto.VerificationDetails;
import io.github.four88labs.corporateBackgroundVerification.dto.VerificationRequest;
import io.github.four88labs.corporateBackgroundVerification.dto.VerificationResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class BgvService {

    private static final Logger logger = LoggerFactory.getLogger(BgvService.class);
    private static final long TIMEOUT_MS = 2000;

    private final RegistryService registryService;
    private final WatchlistService watchlistService;
    private final ObjectMapper objectMapper;

    public VerificationResponse checkOrgHealth(VerificationRequest request) throws Exception {

        Future<String> watchListFuture = watchlistService.getOrgStatus(
                request.getCountryCode(), request.getCompanyName());
        Future<String> registryFuture = registryService.getOrgValidity(
                request.getRegistrationNumber(), request.getCountryCode());

        VerificationDetails watchListDetails = resolveVerificationDetails("Watchlist", watchListFuture);
        VerificationDetails registryDetails = resolveVerificationDetails("CorporateRegistry", registryFuture);

        List<VerificationDetails> verificationDetails = List.of(watchListDetails, registryDetails);

        return VerificationResponse.builder()
                .reference(request.getReference())
                .riskStatus(determineRiskStatus(verificationDetails))
                .processedAt(LocalDateTime.now())
                .verificationDetails(verificationDetails)
                .build();
    }

    private VerificationDetails resolveVerificationDetails(String type, Future<String> future) {
        VerificationDetails details = new VerificationDetails();
        details.setType(type);

        try {
            String response = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            logger.info("{} Response: {}", type, response);
            String status = objectMapper.readTree(response).get("status").asText();
            details.setStatus(status);
            if(details.getStatus().equals("FLAGGED")) {
                details.setError("Multiple entities found for the given name. Active entities found "+objectMapper.readTree(response).get("matchesFound").asText());
            }

        } catch (TimeoutException e) {
            logger.warn("{} call timed out after {}ms", type, TIMEOUT_MS);
            details.setStatus("UNKNOWN");
            details.setError("Service timed out");

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof HttpStatusCodeException ex) {
                logger.error("{} call failed - Status: {}, Message: {}",
                        type, ex.getStatusCode().value(), ex.getStatusText());
                details.setError("Service error: " + ex.getStatusCode().value() + " " + ex.getStatusText());
            } else {
                logger.error("{} call failed unexpectedly: {}", type, cause.getClass().getSimpleName());
                details.setError("Unexpected service error");
            }
            details.setStatus("UNKNOWN");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("{} call was interrupted", type);
            details.setStatus("UNKNOWN");
            details.setError("Service call interrupted");
        }

        return details;
    }

    private String determineRiskStatus(List<VerificationDetails> verificationDetails) {
        boolean hasUnknown = verificationDetails.stream()
                .anyMatch(d -> "UNKNOWN".equals(d.getStatus()));

        boolean hasFlagged = verificationDetails.stream()
                .anyMatch(d -> !Set.of("CLEAN", "ACTIVE", "UNKNOWN").contains(d.getStatus()));

        if (hasFlagged) return "RED";
        if (hasUnknown) return "YELLOW";
        return "GREEN";
    }
}
