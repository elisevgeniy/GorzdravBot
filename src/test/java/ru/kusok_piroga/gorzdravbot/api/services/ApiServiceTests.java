package ru.kusok_piroga.gorzdravbot.api.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kusok_piroga.gorzdravbot.api.models.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApiServiceTests {

    @Value("${oms}")
    private String omsNumber;
    @Value("${patient.name.first}")
    private String patientFirstName;
    @Value("${patient.name.last}")
    private String patientLastName;
    @Value("${patient.name.middle}")
    private String patientMiddleName;
    @Value("${patient.birthdate}")
    private String patientBirthdateStr;
    @Value("${patient.polyclinicId}")
    private Integer patientPolyclinicId;
    @Value("${patient.appointmentId}")
    private String patientAppointmentId;
    @Value("${patient.id}")
    private String patientId;

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
        List<AvailableAppointment> availableAppointments = apiService.getAvailableAppointments(polyclinicId, doctorId);
        assertThat(availableAppointments).withFailMessage("Список талончиков не должен быть пустым")
                .isNotEmpty();
    }

    @Test
    void testGetPatientId(){
        Date patientBirthday = new Date();
        try {
            patientBirthday = (new SimpleDateFormat("yyyy-MM-dd")).parse(patientBirthdateStr);
        } catch (ParseException e) {
            throw new AssertionError("Date parse fail");
        }
        String patient = apiService.getPatientId(patientPolyclinicId, patientFirstName, patientLastName, patientMiddleName, patientBirthday);
        assertThat(patient).withFailMessage("Пациент должен быть найден")
                .isNotBlank();
        assertThat(patient).withFailMessage("Пациент не тот")
                .isEqualTo(patientId);
    }

    @Test
    void testGetFutureAppointments(){
        List<FutureAppointment> futureAppointments = apiService.getFutureAppointments(patientPolyclinicId, patientId);
        assertThat(futureAppointments).withFailMessage("Будущая запись должна быть найдена")
                .isNotEmpty();
    }

    @Test
    void testCreateAppointment(){
        boolean isSuccess = apiService.createAppointment(patientPolyclinicId, patientAppointmentId, patientId);
        assertThat(isSuccess).withFailMessage("Запрос должен обработаться успешно")
                .isTrue();
    }

    @Test
    void testCancelAppointment(){
        boolean isCanceled = apiService.cancelAppointment(patientPolyclinicId, patientAppointmentId, patientId);
        assertThat(isCanceled).withFailMessage("Запись должна отмениться")
                .isTrue();
    }
}
