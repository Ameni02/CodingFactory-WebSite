package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.CV;
import codingfactory.gestion_formation.Repositories.CVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Service

public class CVService {
    @Autowired
    private final CVRepository cvRepository;
    @Autowired
    private final RestTemplate restTemplate;

    public CVService(CVRepository cvRepository, RestTemplate restTemplate) {
        this.cvRepository = cvRepository;
        this.restTemplate = restTemplate;
    }

    public double analyserCV(String filePath) {
        String url = "http://localhost:5000/api/analyse-cv"; // API Python endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Double.class);
        return response.getBody();
    }

    public CV traiterCV(CV cv) {
        double score = analyserCV(cv.getFichierPath());
        cv.setScore(score);
        cv.setValide(score > 50); // Validate if score is greater than 50
        return cvRepository.save(cv);
    }

    // New method to get all valid CVs
    public List<CV> getCVValides() {
        return cvRepository.findByValide(true);
    }
}
