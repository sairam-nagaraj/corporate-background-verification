package io.github.four88labs.corporateBackgroundVerification.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerificationDetails {
    private String type;
    private String status;
    private String error = "";
}
