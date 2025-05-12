package com.quizz.quizz.Service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


@Service
public class DictionaryService {

    private final String DICTIONARY_API = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public String fetchDefinition(String word) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = DICTIONARY_API + word;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JSONArray jsonArray = new JSONArray(response.getBody());
            JSONObject firstEntry = jsonArray.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject meaning = meanings.getJSONObject(0);
            JSONArray definitions = meaning.getJSONArray("definitions");
            JSONObject definition = definitions.getJSONObject(0);

            return definition.getString("definition");
        } catch (Exception e) {
            return "Definition not found.";
        }
    }
}
