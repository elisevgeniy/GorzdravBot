package ru.kusok_piroga.gorzdravbot.domain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    private String lowTimeLimit;

    private String highTimeLimit;

    private Date highDateLimit;

    private Date lastNotify;

    private String recordedAppointmentId;

    @ManyToOne
    @JoinColumn(name="patient_id")
    private PatientEntity patientEntity;

    @Nonnull
    private Boolean completed = false;
}
