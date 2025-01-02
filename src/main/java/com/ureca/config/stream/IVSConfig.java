package com.ureca.config.stream;
/*

@Configuration
public class IVSConfig {

    @Value("${aws.access-key}")
    private String awsAccessKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public IvsClient ivsClient() {
        return IvsClient.builder()
                .region(Region.of(awsRegion)) // 원하는 AWS 리전으로 설정 (예: 서울 리전)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build();
    }

}
*/
