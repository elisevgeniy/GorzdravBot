package ru.kusok_piroga.gorzdravbot.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.kusok_piroga.gorzdravbot.api.models.District;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {
    private static final String URL_DISTRICTS = "https://gorzdrav.spb.ru/_api/api/v2/shared/districts";

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<District> getDistricts(){
        String jsonResponse = webClient
                .get()
                .uri(URL_DISTRICTS)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            return mapper.readValue(rootNode.path("result").toString(), new TypeReference<List<District>>(){});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
