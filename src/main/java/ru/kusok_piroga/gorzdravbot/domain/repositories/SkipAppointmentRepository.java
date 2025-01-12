package ru.kusok_piroga.gorzdravbot.domain.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.kusok_piroga.gorzdravbot.domain.models.SkipAppointmentEntity;

public interface SkipAppointmentRepository extends CrudRepository<SkipAppointmentEntity, Long> {
}