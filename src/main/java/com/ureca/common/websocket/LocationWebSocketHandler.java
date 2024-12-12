package com.ureca.common.websocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            String payload =
                    message.getPayload(); // 예를 들어, "latitude=37.7749&longitude=-122.4194" 이런 형식일 수
            // 있음
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> receivedData = objectMapper.readValue(payload, Map.class);

            // 클라이언트에서 보내는 메시지 형태가 latitude와 longitude를 포함한 경우 처리
            String latitude = null;
            String longitude = null;

            // 예시: 받은 메시지가 위치 데이터 (위도, 경도) 포함했다고 가정
            String[] paramsArray = payload.split("&");
            for (String param : paramsArray) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    if ("latitude".equals(keyValue[0])) {
                        latitude = keyValue[1];
                    } else if ("longitude".equals(keyValue[0])) {
                        longitude = keyValue[1];
                    }
                }
            }

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
            }
        }
    }

    // 쿼리 문자열 파싱 유틸리티
    private Map<String, String> getQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}
