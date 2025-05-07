package codingfactory.gestion_formation.Controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cors-test")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CorsTestController {

    @GetMapping
    public Map<String, String> testCors() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is working!");
        return response;
    }
}
