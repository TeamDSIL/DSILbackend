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
import java.net.URL;
import java.util.Map;

@Service
public class RefundService {

    @Value("${iamport.key}")
    private String iamport_key;

    @Value("${iamport.secret}")
    private String iamport_secret;

    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(iamport_key, iamport_secret);
    }

    public String getToken() throws Exception {
        URL url = new URL("https://api.iamport.kr/users/getToken");
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
        Map<String, Object> responseBody = (Map<String, Object>) responseMap.get("response");

        conn.disconnect();

        if (responseBody == null) {
            throw new RuntimeException("Failed to cancel payment: response body is null");
        }

        return responseBody.toString(); // You might want to return a more specific response depending on your needs
    }
}


