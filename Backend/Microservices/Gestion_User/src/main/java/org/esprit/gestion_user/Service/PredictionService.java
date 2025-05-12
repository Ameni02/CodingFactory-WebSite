package org.esprit.gestion_user.Service;

import org.esprit.gestion_user.Dto.UserPredictionRequest;
import org.esprit.gestion_user.Dto.UserPredictionResponse;
import org.esprit.gestion_user.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PredictionService {

    private final RestTemplate restTemplate;

    // ngrok public URL for FastAPI model
    private final String MODEL_API_URL = "https://205b-34-30-75-102.ngrok-free.app/predict"; // Update this URL

    @Autowired
    public PredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double predictSuccessScore(User user) {
        // Prepare the request for prediction
        UserPredictionRequest request = new UserPredictionRequest(
                user.getAssiduite(),
                user.getNoteProjet(),
                user.getExams(),
                user.getCc()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserPredictionRequest> entity = new HttpEntity<>(request, headers);

        UserPredictionResponse response = restTemplate.exchange(
                MODEL_API_URL,
                HttpMethod.POST,
                entity,
                UserPredictionResponse.class
        ).getBody();

        if (response != null) {
            System.out.println("Prediction response: " + response.getSuccessScore());
            return response.getSuccessScore();
        } else {
            System.out.println("Failed to get prediction response.");
            return 0.0;
        }
    }
}
