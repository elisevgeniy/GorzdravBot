package ru.kusok_piroga.gorzdravbot.api.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kusok_piroga.gorzdravbot.api.models.District;

import java.util.List;

import static org.springframework.util.Assert.notEmpty;

@SpringBootTest
class ApiServiceTests {

    ApiService apiService = new ApiService();

    @Test
    void testDistrictList(){
        List<District> districts = apiService.getDistricts();
        notEmpty(districts, "Список районов не должен быть пустым");
    }
}
