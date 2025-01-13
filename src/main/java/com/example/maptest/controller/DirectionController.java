package com.example.maptest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class DirectionController {
	
	@Value("${naver.api.client-id}")
	String clientId;
	
	@Value("${naver.api.client-secret}")
	String clientSecret;

    @GetMapping("/api/driving")
    public ResponseEntity<String> getDriving(
    		 @RequestParam("departureX") double departureX,
    	     @RequestParam("departureY") double departureY,
    	     @RequestParam("destinationX") double destinationX,
    	     @RequestParam("destinationY") double destinationY
        ) {
    	
    	try {
    		System.out.println("출발 X : " + departureX);
    		System.out.println("출발 Y : " + departureY);
    		System.out.println("도착 X : " + destinationX);
    		System.out.println("도착 Y : " + destinationY);
    		
    		
    		RestTemplate restTemplate = new RestTemplate();
    		String url = UriComponentsBuilder.fromHttpUrl("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving")
                    .queryParam("start", String.format("%f,%f", departureX, departureY))
                    .queryParam("goal", String.format("%f,%f", destinationX, destinationY))
                    .build()
                    .toUriString();

            System.out.println("생성된 URI: " + url);

    		HttpHeaders headers = new HttpHeaders();
    		headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
    		headers.set("X-NCP-APIGW-API-KEY", clientSecret);
    		headers.set("Content-Type", "application/json");
    		
    		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
    		
    		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    		
    		System.out.println("데이터 확인: " + response.getBody());
    		
    		return ResponseEntity.ok(response.getBody());
			
		} catch (Exception e) {
			System.out.println("에러 확인" + e);
			return ResponseEntity.status(500).body("길찾기 요청 실패 : " + e.getMessage() );
		}
    	
    	
    }
}