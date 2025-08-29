package com.example.Spring.Web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Call Generate Webhook API
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> request = new HashMap<>();
        request.put("name", "Chakrish Vejandla");
        request.put("regNo", "22BCE8330");
        request.put("email", "chakrish.22bce8330@vitapstudent.ac.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Webhook Response: " + response.getBody());

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            // TODO: Step 2 → solve your SQL question based on regNo
            String finalQuery = "SELECT e1.EMP_ID, " +
                    "e1.FIRST_NAME, " +
                    "e1.LAST_NAME, " +
                    "d.DEPARTMENT_NAME, " +
                    "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM EMPLOYEE e1 " +
                    "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                    "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
                    "AND e2.DOB > e1.DOB " +
                    "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                    "ORDER BY e1.EMP_ID DESC;";


            // Step 3: Submit SQL query
            submitFinalQuery(webhookUrl, accessToken, finalQuery);
        } else {
            System.out.println("Failed to call webhook API: " + response.getStatusCode());
        }
    }

    private void submitFinalQuery(String webhookUrl, String accessToken, String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Use the token directly (not Bearer)
        headers.set("Authorization", accessToken);

        Map<String, String> request = new HashMap<>();
        request.put("finalQuery", query);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

        System.out.println("Final Query Submission Response: " + response.getBody());
    }

}
