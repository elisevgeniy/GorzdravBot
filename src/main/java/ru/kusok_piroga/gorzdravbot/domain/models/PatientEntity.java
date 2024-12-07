package ru.kusok_piroga.gorzdravbot.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PatientState state;

    private Long dialogId;

    private String firstName;

    private String secondName;

    private String middleName;

    private String patientId;

    private Date birthday;
}
