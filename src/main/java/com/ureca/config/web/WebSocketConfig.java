package com.ureca.config.web;

import com.ureca.common.websocket.LocationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 웹소켓을 활성화하는 어노테이션
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LocationWebSocketHandler(), "/daengggu/websocket")
                .setAllowedOrigins("*"); // CORS 설정: 모든 도메인에서 접근 허용
    }
}
