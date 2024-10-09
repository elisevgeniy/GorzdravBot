package ru.kusok_piroga.gorzdravbot.api.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kusok_piroga.gorzdravbot.api.models.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApiServiceTests {

    @Value("${oms}")
    private String omsNumber;

    ApiService apiService = new ApiService();

    @Test
    void testDistrictList(){
        List<District> districts = apiService.getDistricts();
        assertThat(districts).withFailMessage("Список районов не должен быть пустым").isNotEmpty();
    }

    @Test
    void testPolyclinicListByDistrict(){
        int districtId = 3;
        List<Polyclinic> polyclinics = apiService.getPolyclinicsByDistrict(districtId);
        assertThat(polyclinics).withFailMessage("Список поликлиник не должен быть пустым")
                .isNotEmpty();
        assertThat(polyclinics.get(0).districtId()).withFailMessage("Запрошенный район и район поликлиники не совпадают")
                .isEqualTo(districtId);
    }

    @Test
    void testPolyclinicListByOMS(){
        List<Polyclinic> polyclinics = apiService.getPolyclinicsByOMS(omsNumber);
        assertThat(polyclinics).withFailMessage("Список поликлиник не должен быть пустым")
                .isNotEmpty();
        assertThat(polyclinics.get(0).id()).withFailMessage("Получены невалидные данные")
                .isEqualTo(189);
    }

    @Test
    void testSpecialtyList(){
        int polyclinicId = 1;
        List<Specialty> specialties = apiService.getSpecialties(polyclinicId);
        assertThat(specialties).withFailMessage("Список специальностей не должен быть пустым")
                .isNotEmpty();
        assertThat(specialties.get(0).id()).withFailMessage("Получены невалидные данные")
                .isEqualTo("92134140");
    }

    @Test
    void testDoctorList(){
        int polyclinicId = 1;
        String specialtyId = "92134140";
        List<Doctor> doctors = apiService.getDoctors(polyclinicId, specialtyId);
        assertThat(doctors).withFailMessage("Список врачей не должен быть пустым")
                .isNotEmpty();
        assertThat(doctors.get(0).id()).withFailMessage("Получены невалидные данные")
                .isEqualTo("3439");
    }

    @Test
    void testTimetableList(){
        int polyclinicId = 1;
        String doctorId = "3439";
        List<Timetable> timetables = apiService.getTimetables(polyclinicId, doctorId);
        assertThat(timetables).withFailMessage("Список времени приема не должен быть пустым")
                .isNotEmpty();
    }

    @Test
    void testAppointmentList(){
        int polyclinicId = 1;
        String doctorId = "3439";
        List<Appointment> appointments = apiService.getAppointments(polyclinicId, doctorId);
        assertThat(appointments).withFailMessage("Список талончиков не должен быть пустым")
                .isNotEmpty();
    }
}
