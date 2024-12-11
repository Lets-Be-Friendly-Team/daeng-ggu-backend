package com.ureca.common.websocket;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.socket.WebSocketSession;

public class SessionManager {
    private static final Map<String, WebSocketSession> sessions = new HashMap<>();

    // 세션 추가 (유저 타입을 구분하여 세션을 추가)
    public static void addSession(String reservationId, String userType, WebSocketSession session) {
        String key = reservationId + "-" + userType; // reservationId-guardian 또는 reservationId-user
        sessions.put(key, session);
    }

    // 세션 제거
    public static void removeSession(String reservationId, String userType) {
        String key = reservationId + "-" + userType;
        sessions.remove(key);
    }

    // 세션 가져오기
    public static WebSocketSession getSession(String reservationId, String userType) {
        String key = reservationId + "-" + userType;
        return sessions.get(key);
    }
}
