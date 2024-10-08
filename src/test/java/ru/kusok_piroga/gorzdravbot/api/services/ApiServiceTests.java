package ru.kusok_piroga.gorzdravbot.api.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;

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
    void testPolyclinicListByDistcrict(){
        int districtId = 3;
        List<Polyclinic> polyclinics = apiService.getPolyclinicsByDistrict(districtId);
        assertThat(polyclinics).withFailMessage("Список районов не должен быть пустым")
                .isNotEmpty();
        assertThat(polyclinics.get(0).districtId()).withFailMessage("Запрошенный район и район поликлиники не совпадают")
                .isEqualTo(districtId);
    }

    @Test
    void testPolyclinicListByOMS(){
        List<Polyclinic> polyclinics = apiService.getPolyclinicsByOMS(omsNumber);
        assertThat(polyclinics).withFailMessage("Список районов не должен быть пустым")
                .isNotEmpty();
        assertThat(polyclinics.get(0).id()).withFailMessage("Получены невалидные данные")
                .isEqualTo(189);
    }
}
