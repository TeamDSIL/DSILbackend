package com.ssg.dsilbackend.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class SentimentAnalysisService {
    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
    private static final String CLIENT_ID = "h9pjx1x5uv";
    private static final String CLIENT_SECRET = "QfGJyNOoxjFqfal2qNFfveQEt6nzEPU8QdhwEt7e";

    public String analyzeSentiment(String content) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(API_URL);

        postRequest.addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        postRequest.addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET);
        postRequest.addHeader("Content-Type", "application/json");

        String jsonInputString = "{\"content\":\"" + content + "\"}";
        StringEntity entity = new StringEntity(jsonInputString, StandardCharsets.UTF_8);
        postRequest.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println("API Response: " + jsonResponse); // 응답 로그 출력
            return parseSentiment(jsonResponse);
        }
    }

    private String parseSentiment(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        String sentiment = jsonObject.getJSONObject("document").getString("sentiment");
        return sentiment;
    }
    }


