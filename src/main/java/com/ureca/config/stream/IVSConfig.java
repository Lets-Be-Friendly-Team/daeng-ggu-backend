package com.ureca.config.stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ivs.IvsClient;

    @Configuration
public class IVSConfig {

        @Bean
        public IvsClient ivsClient() {
            return IvsClient.builder()
                    .region(Region.AP_NORTHEAST_2) // 서울 리전
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();
        }

}