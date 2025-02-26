package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.recorder.utils.ConstraintChecker;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class FetchService {
    private final ApiService api;

    /**
     * Gets all available appointments and filters them according to the restrictions specified in the task
     * @param task
     * @return list of available appointments
     * @see ConstraintChecker
     */
    public List<AvailableAppointment> getValidAvailableAppointments(TaskEntity task){
        List<AvailableAppointment> availableAppointments = api.getAvailableAppointments(task.getPolyclinicId(), task.getDoctorId());

        if (availableAppointments.isEmpty()) {
            return emptyList();
        }

        return availableAppointments.stream().filter(
                availableAppointment -> ConstraintChecker.check(task, availableAppointment)
        ).toList();
    }

}
