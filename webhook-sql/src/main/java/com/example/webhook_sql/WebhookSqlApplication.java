package com.example.webhook_sql;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class WebhookSqlApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WebhookSqlApplication.class, args);
	}

	@Override
	public void run(String... args) {
		RestTemplate restTemplate = new RestTemplate();

		// Step 1: Call generateWebhook API
		String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

		Map<String, String> request = new HashMap<>();
		request.put("name", "Supriya Baghel");
		request.put("regNo", "22BCE2720");
		request.put("email", "supriya.baghel2022@vitstudent.ac.in");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

		if (response.getBody() == null) {
			System.err.println("No response received from API.");
			return;
		}

		String webhook = (String) response.getBody().get("webhook");
		String accessToken = (String) response.getBody().get("accessToken");

		System.out.println("Webhook: " + webhook);
		System.out.println("AccessToken: " + accessToken);

		// Step 2: Put the correct SQL query (from your assigned question PDF)
		String finalQuery = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, SUM(CASE WHEN e2.DOB > e.DOB THEN 1 ELSE 0 END) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e JOIN DEPARTMENT d ON d.DEPARTMENT_ID = e.DEPARTMENT LEFT JOIN EMPLOYEE e2 ON e2.DEPARTMENT = e.DEPARTMENT AND e2.EMP_ID <> e.EMP_ID GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, e.DOB ORDER BY e.EMP_ID DESC;";

		// Step 3: Submit SQL to webhook
		Map<String, String> queryBody = new HashMap<>();
		queryBody.put("finalQuery", finalQuery);

		HttpHeaders submitHeaders = new HttpHeaders();
		submitHeaders.setContentType(MediaType.APPLICATION_JSON);
		submitHeaders.set("Authorization", accessToken);

		HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(queryBody, submitHeaders);

		ResponseEntity<String> submitResponse =
				restTemplate.postForEntity(webhook, submitEntity, String.class);

		System.out.println("Submission Response: " + submitResponse.getBody());
	}
}
