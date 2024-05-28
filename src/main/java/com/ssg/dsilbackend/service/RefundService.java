package com.ssg.dsilbackend.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.siot.IamportRestClient.IamportClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 환불을 위해 작성한 클래스로 해당 로직을 구현하기 위해 key,secret 값과 토큰 값이 있어야 구현 가능하다.
 * (포트원 api에 명세되어있음)
 * 작성자 : [Imhwan]
 */
@Service
public class RefundService {

    //value로 가져오는건 properties 설정 파일에 존재함
    @Value("${iamport.key}")
    private String iamport_key;

    @Value("${iamport.secret}")
    private String iamport_secret;

    private IamportClient iamportClient; //해당 객체를 사용하여 api요청을 보냄

    @PostConstruct//빈이 생성된 후 자동으로 수행되게 객체 생성 및 초기화 작업
    public void init() {
        this.iamportClient = new IamportClient(iamport_key, iamport_secret);
    }

    //인증 토큰을 얻는 메소드
    public String getToken() throws Exception {
        URL url = new URL("https://api.iamport.kr/users/getToken"); //토큰을 요청하기 위한 url
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json"    );
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("imp_key", iamport_key);
        json.addProperty("imp_secret", iamport_secret);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
            bw.write(json.toString());
            bw.flush();
        }

        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(response.toString(), Map.class);
        Map<String, String> responseBody = (Map<String, String>) responseMap.get("response");

        if (responseBody == null) {
            throw new RuntimeException("Failed to get token: response body is null");
        }

        String token = responseBody.get("access_token");
        conn.disconnect();

        return token;
    }

    //결제 취소 요청을 위한 메서드
    public String cancelPayment(String imp_uid, String reason, String access_token) throws Exception {
        HttpsURLConnection conn = null;
        URL url = new URL("https://api.iamport.kr/payments/cancel");
        conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + access_token);
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("imp_uid", imp_uid);
        json.addProperty("reason", reason);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
            bw.write(json.toString());
            bw.flush();
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response.toString(), Map.class);
            Map<String, Object> responseBody = (Map<String, Object>) responseMap.get("response");

            if (responseBody == null) {
                throw new RuntimeException("Failed to cancel payment: response body is null");
            }

            return responseBody.toString();
        } else {
            throw new RuntimeException("Failed to cancel payment: HTTP error code - " + responseCode);
        }
    }
}

