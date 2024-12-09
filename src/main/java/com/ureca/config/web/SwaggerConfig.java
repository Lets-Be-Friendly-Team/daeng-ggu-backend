package com.ureca.config.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.protocol}")
    String protocol;

    @Value("${server.host}")
    String host;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addServersItem(new Server().url(protocol + "://" + host).description("https 호스트"))
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("daeng-ggu") // API의 제목
                .description("댕꾸 Swagger UI 입니다.") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
