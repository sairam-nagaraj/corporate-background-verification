package io.github.four88labs.corporateBackgroundVerification.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationResponse {
    private UUID reference;
    private String riskStatus;
    private LocalDateTime processedAt;
    private List<VerificationDetails> verificationDetails;
}
