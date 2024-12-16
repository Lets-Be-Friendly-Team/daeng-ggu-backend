package com.ureca.login.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.login.application.dto.Coordinate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 외부 API 연동 서비스 파일
@Service
public class ExternalService {

    private final RestTemplate restTemplate;

    public ExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${kakao.map.api.key}")
    String kakaoMapApiKey;

    private static final String KAKAO_ADDRESS_URI =
            "https://dapi.kakao.com/v2/local/search/address.json";

    /**
     * @title 주소를 좌표로 변환
     * @return Coordinate x,y 좌표
     */
    public Coordinate addressToCoordinate(String address2) {
        Coordinate coordinate = new Coordinate();
        try {
            String fullUrl = URLEncoder.encode(address2, "UTF-8");
            String addr = KAKAO_ADDRESS_URI + "?query=" + fullUrl;
            URL url = new URL(addr);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", "KakaoAK " + kakaoMapApiKey);

            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer docJson = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) docJson.append(line);

            String jsonString = docJson.toString();
            br.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode documents = rootNode.path("documents");
            JsonNode firstDocument = documents.get(0);

            coordinate.setX(firstDocument.path("x").asDouble());
            coordinate.setY(firstDocument.path("y").asDouble());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return coordinate;
    } // addressToCoordinate
}
