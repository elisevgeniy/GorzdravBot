package ru.kusok_piroga.gorzdravbot.domain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kusok_piroga.gorzdravbot.domain.repositories.converters.TaskDateLimitsConverter;
import ru.kusok_piroga.gorzdravbot.domain.repositories.converters.TaskTimeLimitsConverter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="tasks")
@Getter
@Setter
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    private Long dialogId;

    private Integer districtId;

    private Integer polyclinicId;

    private Integer specialityId;

    private String  doctorId;

    @Convert(converter = TaskTimeLimitsConverter.class)
    private TaskTimeLimits timeLimits;

    @Convert(converter = TaskDateLimitsConverter.class)
    private TaskDateLimits dateLimits;

    private LocalDateTime lastNotify;

    private String recordedAppointmentId;

    @ManyToOne
    @JoinColumn(name="patient_id")
    private PatientEntity patientEntity;

    @Nonnull
    private Boolean completed = false;

    @OneToMany(mappedBy = "task", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<SkipAppointmentEntity> skippedAppointments = new LinkedHashSet<>();

    @Override
    public String toString() {
        return new StringBuffer()
                .append("id=").append(id).append(", ")
                .append("completed=").append(completed).append(", ")
                .append("dialogId=").append(dialogId).append(", ")
                .append("dateLimits=").append(dateLimits).append(", ")
                .append("timeLimits=").append(timeLimits).append(", ")
                .append("lastNotify=").append(lastNotify).append(", ")
                .append("recordedAppointmentId=").append(recordedAppointmentId).append(", ")
                .append("patientEntity id=").append(patientEntity.getId()).append(", ")
                .append("patientEntity id=").append(patientEntity.getId()).append(", ")
                .toString();
    }
}
