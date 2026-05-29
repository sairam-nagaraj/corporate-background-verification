package io.github.four88labs.corporateBackgroundVerification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequest {
    private UUID reference;
    private String companyName;
    private String countryCode;
    private String registrationNumber;
}
