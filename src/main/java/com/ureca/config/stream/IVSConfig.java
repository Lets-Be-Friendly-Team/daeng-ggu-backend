package com.ureca.config.stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ivs.IvsClient;

@Configuration
public class IVSConfig {
    @Bean
    public IvsClient ivsClient() {
        return IvsClient.builder()
                .region(Region.AP_NORTHEAST_2) // 원하는 AWS 리전으로 설정 (예: 서울 리전)
                .build();
    }
}
