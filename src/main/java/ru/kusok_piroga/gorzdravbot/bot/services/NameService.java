package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;

import java.util.Optional;

/**
 * Service for getting some names.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NameService {
    private final ApiService api;

    public String getPolyclinicName(int polyclinicId) {
        Optional<Polyclinic> p = api.getPolyclinic(polyclinicId);
        return p.orElseThrow().lpuFullName();
    }

    public String getSpecialityName(int specialityId, int polyclinicId) {
        var speciality = api.getSpecialties(polyclinicId).stream()
                // TODO: исправить это убожество
                .filter(specialty -> Integer.parseInt(specialty.id()) == specialityId)
                .findFirst();
        if (speciality.isPresent())
            return speciality.get().name();
        else
            return "Не найден или по направлению";
    }

    public String getDoctorName(String doctorId, int polyclinicId, int specialityId) {
        var speciality = api.getSpecialties(polyclinicId).stream()
                // TODO: исправить это убожество
                .filter(specialty -> Integer.parseInt(specialty.id()) == specialityId)
                .findFirst();
        if (speciality.isPresent()) {
            // TODO: исправить это убожество
            var doctorOpt = api.getDoctors(polyclinicId, Integer.toString(specialityId)).stream()
                    .filter(doctor -> doctor.id().equals(doctorId))
                    .findFirst();
            if (doctorOpt.isPresent())
                return doctorOpt.get().name();
            else
                return "Не найден или по направлению";
        } else
            return "Не найден или по направлению";
    }
}
