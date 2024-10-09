package ru.kusok_piroga.gorzdravbot.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.kusok_piroga.gorzdravbot.api.models.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {
    private static final String SCHEME = "https";
    private static final String HOST = "gorzdrav.spb.ru";
    private static final String URL_DISTRICTS = "https://gorzdrav.spb.ru/_api/api/v2/shared/districts";
    private static final String URL_POLYCLINICS_BY_DISTRICT = "https://gorzdrav.spb.ru/_api/api/v2/shared/district/{districtId}/lpus";
    private static final String URL_POLYCLINICS_BY_OMS = "https://gorzdrav.spb.ru/_api/api/v2/oms/attachment/lpus?polisN={polisN}";
    private static final String URL_SPECIALTIES = "https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/{lpuId}/specialties";
    private static final String URL_DOCTORS = "https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/{lpuId}/speciality/{specialtyId}/doctors";
    private static final String URL_TIMETABLES = "https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/{lpuId}/doctor/{doctorId}/timetable";
    private static final String URL_APPOINTMENTS = "https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/{lpuId}/doctor/{doctorId}/appointments";
    private static final String PATH_FIND_PATIENT = "/_api/api/v2/patient/search";

    private final WebClient webClient = WebClient.create();
    private final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

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

    public List<Specialty> getSpecialties(Integer polyclinicId){
        SpecialtiesResponse response = webClient
                .get()
                .uri(URL_SPECIALTIES, polyclinicId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SpecialtiesResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getSpecialties();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Doctor> getDoctors(Integer polyclinicId, String specialtyId){
        DoctorsResponse response = webClient
                .get()
                .uri(URL_DOCTORS, polyclinicId, specialtyId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DoctorsResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getDoctors();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Timetable> getTimetables(Integer polyclinicId, String doctorId){
        TimetablesResponse response = webClient
                .get()
                .uri(URL_TIMETABLES, polyclinicId, doctorId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TimetablesResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getTimetables();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Appointment> getAppointments(Integer polyclinicId, String doctorId){
        AppointmentsResponse response = webClient
                .get()
                .uri(URL_APPOINTMENTS, polyclinicId, doctorId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AppointmentsResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getAppointments();
        } else {
            return Collections.emptyList();
        }
    }

    public String findPatient (Integer polyclinicId, String firstName, String lastName, String middleName, Date birthdate){
        FindPatientResponse response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(SCHEME)
                        .host(HOST)
                        .path(PATH_FIND_PATIENT)
                        .queryParam("lpuId", polyclinicId)
                        .queryParam("firstName", firstName)
                        .queryParam("lastName", lastName)
                        .queryParam("middleName", middleName)
                        .queryParam("birthdate", dateFormater.format(birthdate))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FindPatientResponse.class)
                .block();

        if (response != null && response.isSuccess()){
            return response.getPatient();
        } else {
            return "";
        }
    }
}
