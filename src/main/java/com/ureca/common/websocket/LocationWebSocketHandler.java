package com.ureca.common.websocket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class LocationWebSocketHandler extends TextWebSocketHandler {

    // 웹소켓 연결 시 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 사용자 또는 가디언의 예약 아이디와 타입을 쿼리 파라미터에서 추출
        Map<String, String> parameters = getQueryParams(session.getUri().getQuery());
        String reservationId = parameters.get("reservationId");
        String userType = parameters.get("userType"); // 'guardian' 또는 'user'

        if (reservationId != null && userType != null) {
            // 예약 아이디와 유저 타입을 기반으로 세션을 추가
            SessionManager.addSession(reservationId, userType, session);
            System.out.println(
                    "Session for "
                            + userType
                            + " with reservation "
                            + reservationId
                            + " connected.");
        }
    }

    // 연결 해제 시 호출
    @Override
    public void afterConnectionClosed(
            WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {
        Map<String, String> params = getQueryParams(session.getUri().getQuery());
        String reservationId = params.get("reservationId");
        String userType = params.get("userType");

        if (reservationId != null && userType != null) {
            SessionManager.removeSession(reservationId, userType);
            System.out.println(
                    userType + " session for reservation " + reservationId + " disconnected.");
        }
    }

    // 클라이언트에서 메시지 수신 시 호출
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> params = getQueryParams(session.getUri().getQuery());
        String reservationId = params.get("reservationId");

        if (reservationId != null) {
            // 메시지를 받아오는 부분
            String payload = message.getPayload(); // JSON 형식의 메시지가 전달됨

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> receivedData;
            try {
                receivedData = objectMapper.readValue(payload, Map.class);
            } catch (JsonParseException | JsonMappingException e) {
                // 잘못된 JSON 형식 처리
                session.sendMessage(new TextMessage("Invalid JSON format"));
                return;
            }

            // 위도와 경도 추출
            String latitude =
                    receivedData.get("latitude") != null
                            ? receivedData.get("latitude").toString()
                            : null;
            String longitude =
                    receivedData.get("longitude") != null
                            ? receivedData.get("longitude").toString()
                            : null;

            if (latitude != null && longitude != null) {
                // 위치 정보가 유효한 경우, 이 정보를 사용자에게 전송
                WebSocketSession guardianSession =
                        SessionManager.getSession(reservationId, "GUARDIAN");
                WebSocketSession userSession = SessionManager.getSession(reservationId, "CUSTOMER");

                Map<String, String> locationData = new HashMap<>();
                locationData.put("latitude", latitude);
                locationData.put("longitude", longitude);

                // JSON 문자열로 변환
                String locationMessage = objectMapper.writeValueAsString(locationData);
                //
                //                if (guardianSession != null && guardianSession.isOpen()) {
                //                    guardianSession.sendMessage(new TextMessage(locationMessage));
                //                }

                if (userSession != null && userSession.isOpen()) {
                    userSession.sendMessage(new TextMessage(locationMessage));
                }
            } else {
                // 위도 또는 경도가 없을 경우 처리
                session.sendMessage(new TextMessage("Missing latitude or longitude"));
            }
        } else {
            // reservationId가 없을 경우 처리
            session.sendMessage(new TextMessage("Missing reservationId"));
        }
    }

    // 쿼리 문자열 파싱 유틸리티임
    private Map<String, String> getQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}
