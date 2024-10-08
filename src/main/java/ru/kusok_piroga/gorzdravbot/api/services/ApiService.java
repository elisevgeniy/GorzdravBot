package ru.kusok_piroga.gorzdravbot.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.DistrictsResponse;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.models.PolyclinicsResponse;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {
    private static final String URL_DISTRICTS = "https://gorzdrav.spb.ru/_api/api/v2/shared/districts";
    private static final String URL_POLYCLINICS_BY_DISTRICT = "https://gorzdrav.spb.ru/_api/api/v2/shared/district/{districtId}/lpus";
    private static final String URL_POLYCLINICS_BY_OMS = "https://gorzdrav.spb.ru/_api/api/v2/oms/attachment/lpus?polisN={polisN}";

    private final WebClient webClient = WebClient.create();

    public List<District> getDistricts(){
        DistrictsResponse response = webClient
                .get()
                .uri(URL_DISTRICTS)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DistrictsResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getDistrictList();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Polyclinic> getPolyclinicsByDistrict(int districtId){
        PolyclinicsResponse response = webClient
                .get()
                .uri(URL_POLYCLINICS_BY_DISTRICT, districtId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PolyclinicsResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getPolyclinicList();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Polyclinic> getPolyclinicsByOMS(String omsNumber){
        PolyclinicsResponse response = webClient
                .get()
                .uri(URL_POLYCLINICS_BY_OMS, omsNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PolyclinicsResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getPolyclinicList();
        } else {
            return Collections.emptyList();
        }
    }
}
