package io.github.four88labs.corporateBackgroundVerification.controller;

import io.github.four88labs.corporateBackgroundVerification.dto.VerificationRequest;
import io.github.four88labs.corporateBackgroundVerification.dto.VerificationResponse;
import io.github.four88labs.corporateBackgroundVerification.service.BgvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1")
public class BgvContoller {

    private BgvService bgvService;

    public BgvContoller(BgvService bgvService) {
        this.bgvService = bgvService;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verify(@RequestBody VerificationRequest request) throws Exception {
        return ResponseEntity.ok(bgvService.checkOrgHealth(request));
    }
}
