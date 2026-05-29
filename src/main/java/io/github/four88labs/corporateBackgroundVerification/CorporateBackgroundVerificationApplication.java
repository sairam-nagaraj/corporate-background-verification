package io.github.four88labs.corporateBackgroundVerification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CorporateBackgroundVerificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorporateBackgroundVerificationApplication.class, args);
	}

}
