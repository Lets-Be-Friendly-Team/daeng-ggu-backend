package com.ureca.login.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.login.application.dto.BusinessRequest;
import com.ureca.login.application.dto.BusinessResponse;
import com.ureca.login.application.dto.Coordinate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 외부 API 연동 서비스 파일
@Service
public class ExternalService {

    @Value("${kakao.map.api.key}")
    String kakaoMapApiKey;

    @Value("${public.data.api.key}")
    String publicDataApiKey;

    private static final String KAKAO_ADDRESS_URI =
            "https://dapi.kakao.com/v2/local/search/address.json";

    private static final String PUBLIC_DATA_URI =
            "https://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=";

    private static final String VALID_CODE = "01";

    /**
     * @title 사업자 번호 인증
     * @param businessNumber 사업자번호( - 없이 )
     * @param representativeName 대표자성명
     * @param startDate 개업일자(YYYYMMDD)
     * @return 인증여부(Y/N)
     */
    public String validateBusinessInfo(
            String businessNumber, String representativeName, String startDate)
            throws URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BusinessRequest.Business business =
                BusinessRequest.Business.builder()
                        .bNo(businessNumber)
                        .startDt(startDate)
                        .pNm(representativeName)
                        .pNm2("")
                        .bNm("")
                        .corpNo("")
                        .bSector("")
                        .bType("")
                        .bAdr("")
                        .build();
        BusinessRequest businessRequest =
                BusinessRequest.builder().businesses(Collections.singletonList(business)).build();

        HttpEntity<BusinessRequest> entity = new HttpEntity<>(businessRequest, headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = PUBLIC_DATA_URI + publicDataApiKey;
        URI uri = new URI(url);

        ResponseEntity<BusinessResponse> response =
                restTemplate.exchange(uri, HttpMethod.POST, entity, BusinessResponse.class);

        String businessIsVerified = "N";
        BusinessResponse businessResponse = response.getBody();
        if (businessResponse != null && !businessResponse.getData().isEmpty()) {
            if (VALID_CODE.equals(businessResponse.getData().get(0).getValid())) {
                businessIsVerified = "Y";
            }
        }
        return businessIsVerified;
    } // validateBusinessInfo

    /**
     * @title 주소를 좌표로 변환
     * @param address2 주소
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
