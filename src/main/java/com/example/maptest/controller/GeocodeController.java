package com.example.maptest.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api")
@RestController
public class GeocodeController {
	
	@RequestMapping("/test/{query}")
	public void test(@PathVariable("query") String query) {
		System.out.println("#########" + query);
	}
	
	@Value("${naver.api.client-id}")
	String clientId;
	
	@Value("${naver.api.client-secret}")
	String clientSecret;
	
	@RequestMapping("/geocode/{query}")
	public ResponseEntity<Object> getGeocode(@PathVariable("query") String query) {
	    try {
	    	System.out.println("백엔드 코드 탐");
	    	// RestTemplate는 Spring framework에서 제공하는 HTTP 클라이언트 클래스 -> RESTful 웹 서비스 호출 가능
	        RestTemplate restTemplate = new RestTemplate();
	        String encodedQuery = URLEncoder.encode(query, "UTF-8"); // 검색값 엔코딩
	        URI uri = new URI("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + encodedQuery);
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
	        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
	        headers.set("Content-Type", "application/json");
	        
	        // 파리미터 1 : "parameters" 본문, 파라미터 2: 헤더 로 객체 생성해서 전달
	        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
	        // restTemplate.exchange를 통해서 http 요청하고 값을 받아서 ResponseEntity에 저장
	        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
	        
	        System.out.println(response.getBody());
	        System.out.println(response.getHeaders());
	        System.out.println(response.toString());
	        
	        // 응답을 받으면
	        if (response.getStatusCode() == HttpStatus.OK) {
	        	 System.out.println("실행되었음");
	            String responseBody = response.getBody();
	            JSONObject jsonResponse = new JSONObject(responseBody);
	            JSONArray addresses = jsonResponse.getJSONArray("addresses");

	            if (addresses.length() > 0) {
	                JSONObject firstAddress = addresses.getJSONObject(0);
	                String x = firstAddress.getString("x");
	                String y = firstAddress.getString("y");
	                
	                System.out.println("#### x 값" + x);
	                System.out.println("#### y 값" + y);

	                Map<String, String> resultMap = new HashMap<>();
	                resultMap.put("x", x);
	                resultMap.put("y", y);

	                return ResponseEntity.ok(resultMap);
	            } else {
	                return ResponseEntity.ok("좌표를 찾을 수 없습니다.");
	            }
	        } else {
	            return ResponseEntity.status(response.getStatusCode()).body("Error occurred");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
	    }
	}

}
